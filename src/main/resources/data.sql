-- GENRES
MERGE INTO genres (id, name) KEY(id) VALUES (1,'COMEDY');
MERGE INTO genres (id, name) KEY(id) VALUES (2,'DRAMA');
MERGE INTO genres (id, name) KEY(id) VALUES (3,'CARTOON');
MERGE INTO genres (id, name) KEY(id) VALUES (4,'THRILLER');
MERGE INTO genres (id, name) KEY(id) VALUES (5,'DOCUMENTARY');
MERGE INTO genres (id, name) KEY(id) VALUES (6,'ACTION');

-- MPA RATINGS
MERGE INTO mpa_ratings (id, name, description) KEY(id) VALUES (1,'G',    'Подходит для любой аудитории');
MERGE INTO mpa_ratings (id, name, description) KEY(id) VALUES (2,'PG',   'Рекомендуется присутствие родителей');
MERGE INTO mpa_ratings (id, name, description) KEY(id) VALUES (3,'PG13', 'До 13 лет — с родителями');
MERGE INTO mpa_ratings (id, name, description) KEY(id) VALUES (4,'R',    'До 17 лет — с родителем/опекуном');
MERGE INTO mpa_ratings (id, name, description) KEY(id) VALUES (5,'NC17', 'Только для взрослых (18+)');
