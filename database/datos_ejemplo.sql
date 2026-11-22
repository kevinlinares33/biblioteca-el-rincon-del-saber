-- =====================================================================
--  Datos de ejemplo - Biblioteca "El Rincón del Saber" (empresa hipotética)
--  OJO: estos datos YA están cargados en su base. No lo vuelva a ejecutar,
--  porque repetiría los libros. Sirve solo para llenar una base nueva y vacía.
-- =====================================================================
USE biblioteca_db;

-- Corrige los libros de prueba que ya estaban
UPDATE libros SET anio = 1967 WHERE titulo = '100 años de soledad';
UPDATE libros SET titulo = 'Cien años de soledad' WHERE titulo = '100 años de soledad';
UPDATE libros SET autor = 'Upton Sinclair', anio = 1906 WHERE titulo = 'la jungla';
UPDATE libros SET titulo = 'La jungla' WHERE titulo = 'la jungla';
UPDATE libros SET autor = 'Rudyard Kipling', anio = 1894 WHERE titulo = 'el libro de la selva';
UPDATE libros SET titulo = 'El libro de la selva' WHERE titulo = 'el libro de la selva';

-- 26 libros nuevos (con los 4 que ya había quedan 30)
INSERT INTO libros (titulo, autor, anio, cantidad) VALUES
('El amor en los tiempos del cólera', 'Gabriel García Márquez', 1985, 3),
('Crónica de una muerte anunciada', 'Gabriel García Márquez', 1981, 4),
('El coronel no tiene quien le escriba', 'Gabriel García Márquez', 1961, 2),
('La vorágine', 'José Eustasio Rivera', 1924, 2),
('María', 'Jorge Isaacs', 1867, 3),
('Delirio', 'Laura Restrepo', 2004, 2),
('El olvido que seremos', 'Héctor Abad Faciolince', 2006, 3),
('Rosario Tijeras', 'Jorge Franco', 1999, 2),
('La casa de los espíritus', 'Isabel Allende', 1982, 3),
('Rayuela', 'Julio Cortázar', 1963, 2),
('Pedro Páramo', 'Juan Rulfo', 1955, 3),
('Ficciones', 'Jorge Luis Borges', 1944, 2),
('La ciudad y los perros', 'Mario Vargas Llosa', 1963, 2),
('El túnel', 'Ernesto Sabato', 1948, 2),
('La sombra del viento', 'Carlos Ruiz Zafón', 2001, 4),
('El principito', 'Antoine de Saint-Exupéry', 1943, 5),
('1984', 'George Orwell', 1949, 4),
('Rebelión en la granja', 'George Orwell', 1945, 3),
('Orgullo y prejuicio', 'Jane Austen', 1813, 2),
('Crimen y castigo', 'Fiódor Dostoyevski', 1866, 2),
('La metamorfosis', 'Franz Kafka', 1915, 3),
('El viejo y el mar', 'Ernest Hemingway', 1952, 2),
('Fahrenheit 451', 'Ray Bradbury', 1953, 3),
('El alquimista', 'Paulo Coelho', 1988, 4),
('Harry Potter y la piedra filosofal', 'J. K. Rowling', 1997, 5),
('El diario de Ana Frank', 'Ana Frank', 1947, 3);

-- Préstamos de ejemplo (algunos devueltos y otros pendientes)
INSERT INTO prestamos (libro_id, persona, fecha_prestamo, fecha_devolucion, devuelto)
SELECT id, 'María Fernanda Rojas', '2026-11-03', '2026-11-10', TRUE FROM libros WHERE titulo = 'El principito';
INSERT INTO prestamos (libro_id, persona, fecha_prestamo, fecha_devolucion, devuelto)
SELECT id, 'Andrés Felipe Suárez', '2026-11-04', '2026-11-12', TRUE FROM libros WHERE titulo = '1984';
INSERT INTO prestamos (libro_id, persona, fecha_prestamo, fecha_devolucion, devuelto)
SELECT id, 'Laura Camila Pérez', '2026-11-06', '2026-11-13', TRUE FROM libros WHERE titulo = 'Pedro Páramo';
INSERT INTO prestamos (libro_id, persona, fecha_prestamo, fecha_devolucion, devuelto)
SELECT id, 'Juan Sebastián Gómez', '2026-11-10', NULL, FALSE FROM libros WHERE titulo = 'La sombra del viento';
INSERT INTO prestamos (libro_id, persona, fecha_prestamo, fecha_devolucion, devuelto)
SELECT id, 'Valentina Ortiz', '2026-11-12', NULL, FALSE FROM libros WHERE titulo = 'Harry Potter y la piedra filosofal';
INSERT INTO prestamos (libro_id, persona, fecha_prestamo, fecha_devolucion, devuelto)
SELECT id, 'Carlos Mario Díaz', '2026-11-14', '2026-11-18', TRUE FROM libros WHERE titulo = 'El alquimista';
INSERT INTO prestamos (libro_id, persona, fecha_prestamo, fecha_devolucion, devuelto)
SELECT id, 'Natalia Castro', '2026-11-17', NULL, FALSE FROM libros WHERE titulo = 'Rayuela';
INSERT INTO prestamos (libro_id, persona, fecha_prestamo, fecha_devolucion, devuelto)
SELECT id, 'Santiago Herrera', '2026-11-19', NULL, FALSE FROM libros WHERE titulo = 'Crónica de una muerte anunciada';

-- Los préstamos pendientes ya tienen el libro afuera: se descuenta 1 ejemplar a cada uno
UPDATE libros SET cantidad = cantidad - 1
WHERE titulo IN ('La sombra del viento', 'Harry Potter y la piedra filosofal', 'Rayuela', 'Crónica de una muerte anunciada');

-- Contraseña del administrador más segura (la aplicación la cifra con BCrypt al arrancar)
UPDATE usuarios SET clave = 'Admin2026', intentos_fallidos = 0 WHERE usuario = 'admin';
