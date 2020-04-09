/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jupiter.math.filters;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.SPACE;

import java.io.Serializable;

import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.entity.Scalar;

public class KalmanFilter
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The estimated state {@link Entity}.
	 */
	public Entity x;
	/**
	 * The state transition {@link Matrix} (i.e. model the transitions between the states).
	 */
	public Entity F;
	/**
	 * The control variables {@link Entity}.
	 */
	public Entity u;
	/**
	 * The control {@link Matrix} (i.e. map the control variables to the state variables).
	 */
	public Entity B;
	/**
	 * The state variance {@link Matrix} (i.e. error of estimation).
	 */
	public Entity P;
	/**
	 * The process variance {@link Matrix} (i.e. error due to process).
	 */
	public Entity Q;
	/**
	 * The measurement {@link Matrix} (i.e. map measurements onto state).
	 */
	public Entity H;
	/**
	 * The Kalman gain {@link Entity}.
	 */
	public Entity K;
	/**
	 * The measurement variance {@link Matrix} (i.e. error from measurements).
	 */
	public Entity R;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link KalmanFilter} by default.
	 */
	public KalmanFilter() {
		x = new Scalar(0.);
		F = new Scalar(1.);
		u = new Scalar(0.);
		B = new Scalar(0.);
		P = new Scalar(1.);
		Q = new Scalar(0.);
		H = new Scalar(1.);
		K = new Scalar(0.);
		R = new Scalar(1.);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Predicts the estimation {@code x} and the state variance matrix {@code P} (a priori).
	 */
	public void predict() {
		// Compute x = F x + B u
		x = F.times(x);
		if (!(x instanceof Matrix && B instanceof Scalar)) {
			x = x.plus(B.times(u));
			IO.debug("x = F x + B u = ", F, " ", x, " + ", B, " ", u, " = ", x);
		} else {
			IO.debug("x = F x = ", F, " ", x, " = ", x);
		}
		// Compute P = F P F' + Q
		P = F.times(P).times(F.transpose()).plus(Q);
		IO.debug("P = F P F' + Q = ", F, " ", P, " ", F, "' + ", Q, " = ", P);
	}

	/**
	 * Updates the estimation {@code x}, the Kalman gain {@code K} and the state variance matrix
	 * {@code P} (a posteriori).
	 * <p>
	 * @param y the measurement {@link Entity}
	 */
	public void update(final Entity y) {
		// Compute K = P H' inv(H P H' + R),
		// where (H P H' + R) is the innovation covariance
		K = P.times(H.transpose()).times(H.times(P).times(H.transpose()).plus(R).inverse());
		IO.debug("K = P H' inv(H P H' + R) = ",
				P, SPACE, H, "' inv(", H, SPACE, P, SPACE, H, "' + ", R, ") = ",
				K);
		// Compute x = x + K (y - H x),
		// where (y - H x) is the innovation
		x = x.plus(K.times(y.minus(H.times(x))));
		IO.debug("x = x + K (y - H x) = ", x, " + ", K, " (", y, " - ", H, SPACE, x, ") = ", x);
		// Compute P = (I - K H) P
		final Entity KH = K.times(H);
		final Entity I = KH.identity();
		IO.debug("P = (I - K H) P = (", I, " - ", K, SPACE, H, ") ", P, " = ", P);
		P = I.minus(KH).times(P);
	}
}
