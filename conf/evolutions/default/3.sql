# Play Jug 0.0.2 Schema

# --- !Ups

COMMENT ON COLUMN "User".email IS 'input=email, size=100';

COMMENT ON COLUMN "event".description IS 'input=textarea, cols=100, rows=8';

    
# --- !Downs

    