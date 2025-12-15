#include <cuda_runtime.h>
#include <cuda.h>
#include <cstdio>

// Mandelbrot kernel (float precision)
extern "C" __global__
__launch_bounds__(256, 2)
void mandelbrotKernel(
    float cx0, float cy0, float scale,
    int width, int height,
    float hw, float hh,
    int maxIter,
    int* __restrict__ output) {

    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x >= width || y >= height) return;

    float cx = cx0 + (x - hw) * scale;
    float cy = cy0 + (y - hh) * scale;

    float zx = 0.0f;
    float zy = 0.0f;
    float zx2 = 0.0f;
    float zy2 = 0.0f;

    int iter = 0;

    #pragma unroll 4
    while ((zx2 + zy2 <= 4.0f) & (iter < maxIter)) {
        zy = fmaf(2.0f * zx, zy, cy);
        zx = zx2 - zy2 + cx;

        zx2 = zx * zx;
        zy2 = zy * zy;
        iter++;
    }

    output[y * width + x] = iter;
}

// Host API
extern "C" __declspec(dllexport)
void mandelbrot(
    float cx0, float cy0, float scale,
    int width, int height,
    int maxIter,
    int* hostOutput) {

    static int* deviceOutput = nullptr;
    static size_t allocatedBytes = 0;

    const size_t neededBytes = (size_t)width * height * sizeof(int);

    if (neededBytes > allocatedBytes) {
        if (deviceOutput) cudaFree(deviceOutput);
        cudaMalloc(&deviceOutput, neededBytes);
        allocatedBytes = neededBytes;
    }

    dim3 block(16, 16);
    dim3 grid(
        (width  + block.x - 1) / block.x,
        (height + block.y - 1) / block.y
    );

    const float hw = 0.5f * width;
    const float hh = 0.5f * height;

    mandelbrotKernel<<<grid, block>>>(
        cx0, cy0, scale,
        width, height,
        hw, hh,
        maxIter,
        deviceOutput
    );

    cudaMemcpy(
        hostOutput,
        deviceOutput,
        neededBytes,
        cudaMemcpyDeviceToHost
    );
}
