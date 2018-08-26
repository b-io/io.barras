__kernel void plus(__global const double* A, __global const double* B, __global double* C) {
	// Find the index in the global arrays
	const int index = get_global_id(0);

	// Compute the addition
	C[index] = A[index] + B[index];
}

__kernel void minus(__global const double* A, __global const double* B, __global double* C) {
	// Find the index in the global arrays
	const int index = get_global_id(0);

	// Compute the addition
	C[index] = A[index] - B[index];
}

__kernel void times(__global const double* A, __global const double* B, __global double* C,
		const int aColumnDimension, const int bColumnDimension) {
	// Find the index in the global arrays
	const int index = get_global_id(0);

	// Find the offsets
	const int rowOffset = index / bColumnDimension;
	const int columnOffset = index % bColumnDimension;

	// Compute the dot product
	double sum = 0.0;
	for (int i = 0; i < aColumnDimension; ++i) {
		sum += A[rowOffset * aColumnDimension + i] * B[i * bColumnDimension + columnOffset];
	}
	C[index] = sum;
}

__kernel void arrayTimes(__global const double* A, __global const double* B, __global double* C) {
	// Find the index in the global arrays
	const int index = get_global_id(0);

	// Compute the multiplication
	C[index] = A[index] * B[index];
}

__kernel void arrayDivision(__global const double* A, __global const double* B, __global double* C) {
	// Find the index in the global arrays
	const int index = get_global_id(0);

	// Compute the multiplication
	C[index] = A[index] / B[index];
}

__kernel void arrayLeftDivision(__global const double* A, __global const double* B, __global double* C) {
	// Find the index in the global arrays
	const int index = get_global_id(0);

	// Compute the multiplication
	C[index] = B[index] / A[index];
}
