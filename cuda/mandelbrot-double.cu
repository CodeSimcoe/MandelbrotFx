#include <cuda_runtime.h>
#include <cuda.h>
#include <cstdio>

// Mandelbrot kernel (double precision)
extern "C" __global__
__launch_bounds__(256, 2)
void mandelbrotKernel(
    double cx0, double cy0, double scale,
    int width, int height,
    double hw, double hh,
    int maxIter,
    int* __restrict__ output) {

    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x >= width || y >= height) return;

    double cx = cx0 + (x - hw) * scale;
    double cy = cy0 + (y - hh) * scale;

    double zx  = 0.0;
    double zy  = 0.0;
    double zx2 = 0.0;
    double zy2 = 0.0;

    int iter = 0;

    #pragma unroll 4
    while ((zx2 + zy2 <= 4.0) & (iter < maxIter)) {
        zy = fma(2.0 * zx, zy, cy);
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
    double cx0, double cy0, double scale,
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

    const double hw = 0.5 * width;
    const double hh = 0.5 * height;

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
