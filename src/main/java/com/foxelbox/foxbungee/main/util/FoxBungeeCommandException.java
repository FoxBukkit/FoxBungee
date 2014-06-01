/**
 * This file is part of FoxBungee.
 *
 * FoxBungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBungee.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbungee.main.util;

public class FoxBungeeCommandException extends Exception {
	private static final long serialVersionUID = 1L;

	public FoxBungeeCommandException(String message, Throwable cause) {
		super(message, cause);
	}

	public FoxBungeeCommandException(String message) {
		super(message);
	}

	public FoxBungeeCommandException(Throwable cause) {
		super(cause);
	}
}
