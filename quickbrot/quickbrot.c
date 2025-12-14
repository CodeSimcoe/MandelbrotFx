#include <immintrin.h>
#include <stddef.h>

#ifdef _WIN32
#define EXPORT __declspec(dllexport)
#else
#define EXPORT
#endif

// Adapted from ttps://github.com/voldemoriarty/Qbrot
EXPORT void mandelbrotLineAVX(
	int y, int width, int max,
    float xl, float yl,
    float xres, float yres,
    int* iterationsPixelsRow) {

    __m256 x01 = _mm256_add_ps(_mm256_set1_ps(xl),
        _mm256_setr_ps(0, xres, 2*xres, 3*xres, 4*xres, 5*xres, 6*xres, 7*xres));
    __m256 x02 = _mm256_add_ps(_mm256_set1_ps(8*xres + xl),
        _mm256_setr_ps(0, xres, 2*xres, 3*xres, 4*xres, 5*xres, 6*xres, 7*xres));
    __m256 y0v = _mm256_set1_ps(y * yres - yl);

    for (int xc = 0; xc < width; xc += 16) {
        __m256 x1 = _mm256_setzero_ps(), y1 = _mm256_setzero_ps();
        __m256 x2 = _mm256_setzero_ps(), y2 = _mm256_setzero_ps();
        __m256i itr1 = _mm256_setzero_si256(), itr2 = _mm256_setzero_si256();

        int cond1 = 1, cond2 = 1;
        while (cond1 | cond2) {
            __m256 xx1 = _mm256_mul_ps(x1, x1), yy1 = _mm256_mul_ps(y1, y1);
            __m256 xy1 = _mm256_add_ps(_mm256_mul_ps(x1, y1), _mm256_mul_ps(x1, y1));
            __m256 xn1 = _mm256_sub_ps(xx1, yy1);
            x1 = _mm256_add_ps(xn1, x01);
            y1 = _mm256_add_ps(xy1, y0v);
            __m256 ab1 = _mm256_add_ps(xx1, yy1);

            __m256 xx2 = _mm256_mul_ps(x2, x2), yy2 = _mm256_mul_ps(y2, y2);
            __m256 xy2 = _mm256_add_ps(_mm256_mul_ps(x2, y2), _mm256_mul_ps(x2, y2));
            __m256 xn2 = _mm256_sub_ps(xx2, yy2);
            x2 = _mm256_add_ps(xn2, x02);
            y2 = _mm256_add_ps(xy2, y0v);
            __m256 ab2 = _mm256_add_ps(xx2, yy2);

            __m256i aCmp1 = _mm256_castps_si256(_mm256_cmp_ps(ab1, _mm256_set1_ps(4), _CMP_LT_OQ));
            __m256i aCmp2 = _mm256_castps_si256(_mm256_cmp_ps(ab2, _mm256_set1_ps(4), _CMP_LT_OQ));
            __m256i iCmp1 = _mm256_cmpeq_epi32(itr1, _mm256_set1_epi32(max));
            __m256i iCmp2 = _mm256_cmpeq_epi32(itr2, _mm256_set1_epi32(max));
            cond1 = _mm256_testc_si256(iCmp1, aCmp1) == 0;
            cond2 = _mm256_testc_si256(iCmp2, aCmp2) == 0;

            __m256i inc1 = _mm256_andnot_si256(iCmp1, aCmp1);
            __m256i inc2 = _mm256_andnot_si256(iCmp2, aCmp2);
            itr1 = _mm256_sub_epi32(itr1, inc1);
            itr2 = _mm256_sub_epi32(itr2, inc2);
        }

        int res[16];
        _mm256_storeu_si256((__m256i*)res, itr1);
        _mm256_storeu_si256((__m256i*)(res + 8), itr2);
        for (int c = 0; c < 16; ++c) {
            iterationsPixelsRow[xc + c] = res[c];
        }

        x01 = _mm256_add_ps(x01, _mm256_set1_ps(16 * xres));
        x02 = _mm256_add_ps(x02, _mm256_set1_ps(16 * xres));
    }
}
