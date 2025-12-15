nvcc -O3 --use_fast_math --shared mandelbrot-float.cu -gencode arch=compute_86,code=sm_86 -Xcompiler "/MD" -o mandelbrot-cuda-float.dll
nvcc -O3 --use_fast_math --shared mandelbrot-double.cu -gencode arch=compute_86,code=sm_86 -Xcompiler "/MD" -o mandelbrot-cuda-double.dll
