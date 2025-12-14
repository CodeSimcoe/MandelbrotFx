#include <cuda_runtime.h>

extern "C" __global__
void mandelbrotKernelTile(
    float cx0, float cy0, float scale,
    int width, int height,
    int maxIter,
    int x0, int y0, int tileWidth, int tileHeight,
    int* output) {

    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x >= tileWidth || y >= tileHeight) return;

    int globalX = x0 + x;
    int globalY = y0 + y;

    if (globalX >= width || globalY >= height) return;

    float cx = cx0 + (globalX - width * 0.5f) * scale;
    float cy = cy0 + (globalY - height * 0.5f) * scale;

    float zx = 0.0f;
    float zy = 0.0f;
    int iter = 0;

    float zx2 = 0.0f;
    float zy2 = 0.0f;

    while (zx2 + zy2 <= 4.0f && iter < maxIter) {
        float xy = zx * zy;
        float zxNew = zx2 - zy2 + cx;
        float zyNew = fmaf(2.0f, xy, cy);

        zx = zxNew;
        zy = zyNew;
        iter++;

        zx2 = zx * zx;
        zy2 = zy * zy;
    }

    output[globalY * width + globalX] = iter;
}

#include <cuda_runtime.h>

extern "C" __declspec(dllexport)
void mandelbrot(
    float cx0, float cy0, float scale,
    int width, int height,
    int maxIter,
    int* hostOutput) {

    int* deviceOutput = nullptr;

    // Allocate device memory
    cudaMalloc(&deviceOutput, width * height * sizeof(int));

    int tileSize = 512; // adjustable tile size
    dim3 block(16,16);

    // Loop over tiles
    for (int y0 = 0; y0 < height; y0 += tileSize) {
        for (int x0 = 0; x0 < width; x0 += tileSize) {
            int tw = min(tileSize, width - x0);
            int th = min(tileSize, height - y0);
            dim3 grid((tw + block.x - 1) / block.x, (th + block.y - 1) / block.y);

            mandelbrotKernelTile<<<grid, block>>>(
                cx0, cy0, scale,
                width, height,
                maxIter,
                x0, y0, tw, th,
                deviceOutput
            );

            // Optional: synchronize per tile to ensure completion
            cudaDeviceSynchronize();
        }
    }

    // Copy results back to host
    cudaMemcpy(hostOutput, deviceOutput, width * height * sizeof(int), cudaMemcpyDeviceToHost);

    cudaFree(deviceOutput);
}

