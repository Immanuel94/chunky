/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class EndRodModel {
	private static Quad[] up = {
			// Side faces.
			new Quad(new Vector3d(7/16., 1/16., 9/16.), new Vector3d(9/16., 1/16., 9/16.),
					new Vector3d(7/16., 1, 9/16.), new Vector4d(0, 2/16., 1/16., 1)),
			new Quad(new Vector3d(9/16., 1/16., 7/16.), new Vector3d(7/16., 1/16., 7/16.),
					new Vector3d(9/16., 1, 7/16.), new Vector4d(2/16., 0, 1/16., 1)),
			new Quad(new Vector3d(7/16., 1/16., 7/16.), new Vector3d(7/16., 1/16., 9/16.),
					new Vector3d(7/16., 1, 7/16.), new Vector4d(0, 2/16., 1/16., 1)),
			new Quad(new Vector3d(9/16., 1/16., 9/16.), new Vector3d(9/16., 1/16., 7/16.),
					new Vector3d(9/16., 1, 9/16.), new Vector4d(2/16., 0, 1/16., 1)),
			// Top face.
			new Quad(new Vector3d(7/16., 1, 7/16.), new Vector3d(7/16., 1, 9/16.),
					new Vector3d(9/16., 1, 7/16.), new Vector4d(2/16., 4/16., 14/16., 1)),
			// Base top face.
			new Quad(new Vector3d(6/16., 1/16., 6/16.), new Vector3d(6/16., 1/16., 10/16.),
					new Vector3d(10/16., 1/16., 6/16.), new Vector4d(2/16., 6/16., 10/16., 14/16.)),
			// Base bottom face.
			new Quad(new Vector3d(10/16., 0, 6/16.), new Vector3d(10/16., 0, 10/16.),
					new Vector3d(6/16., 0, 6/16.), new Vector4d(6/16., 2/16., 10/16., 14/16.)),
			// Bottom side faces.
			new Quad(new Vector3d(6/16., 0, 10/16.), new Vector3d(10/16., 0, 10/16.),
					new Vector3d(6/16., 1/16., 10/16.), new Vector4d(2/16., 4/16., 9/16., 10/16.)),
			new Quad(new Vector3d(10/16., 0, 6/16.), new Vector3d(6/16., 0, 6/16.),
					new Vector3d(10/16., 1/16., 6/16.), new Vector4d(4/16., 2/16., 9/16., 10/16.)),
			new Quad(new Vector3d(6/16., 0, 6/16.), new Vector3d(6/16., 0, 10/16.),
					new Vector3d(6/16., 1/16., 6/16.), new Vector4d(2/16., 4/16., 9/16., 10/16.)),
			new Quad(new Vector3d(10/16., 0, 10/16.), new Vector3d(10/16., 0, 6/16.),
					new Vector3d(10/16., 1/16., 10/16.), new Vector4d(4/16., 2/16., 9/16., 10/16.)),
	};

	private static Quad[][] facing = new Quad[6][];

	static {
		Quad[] down = Model.rotateX(Model.rotateX(up));
		Quad[] east = Model.rotateZ(down);
		Quad[] south = Model.rotateY(east);
		Quad[] west = Model.rotateY(south);
		Quad[] north = Model.rotateY(west);

		facing[0] = down;
		facing[1] = up;
		facing[2] = north;
		facing[3] = south;
		facing[4] = west;
		facing[5] = east;
	}

	public static boolean intersect(Ray ray) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : facing[ray.getBlockData() % 6]) {
			if (quad.intersect(ray)) {
				Texture.endRod.getColor(ray);
				ray.t = ray.tNext;
				ray.n.set(quad.n);
				hit = true;
			}
		}
		if (hit) {
			ray.distance += ray.t;
			ray.o.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}
