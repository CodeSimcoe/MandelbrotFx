nvcc -O3 --use_fast_math --shared mandelbrot.cu -gencode arch=compute_86,code=sm_86 -Xcompiler "/MD" -o mandelbrot_cuda.dll

::nvcc -O3 --shared mandelbrot.cu -gencode arch=compute_86,code=sm_86 -Xcompiler "/MD" -o mandelbrot_cuda.dll
::nvcc -O3 --use_fast_math -Xptxas=-v mandelbrot.cu -shared -o mandelbrot.dll
