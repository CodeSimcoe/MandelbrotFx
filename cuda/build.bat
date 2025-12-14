nvcc -O3 --shared mandelbrot.cu -gencode arch=compute_86,code=sm_86 -Xcompiler "/MD" -o mandelbrot_cuda.dll
