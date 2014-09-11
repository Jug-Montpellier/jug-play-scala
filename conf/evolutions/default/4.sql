# Play Jug 0.0.2 Schema

# --- !Ups

ALTER TABLE talk
   ALTER COLUMN teaser TYPE text;

# --- !Downs

ALTER TABLE talk
   ALTER COLUMN teaser TYPE varchar(255);
