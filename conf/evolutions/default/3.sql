# Play Jug 0.0.2 Schema

# --- !Ups

COMMENT ON COLUMN "User".email IS 'input=email, size=100';

COMMENT ON COLUMN "event".description IS 'input=textarea, cols=70, rows=8';

COMMENT ON TABLE "User" IS 'crude=true';
COMMENT ON TABLE "speaker" IS 'crude=true';
COMMENT ON COLUMN "speaker".photourl IS 'input=url, render=img';
COMMENT ON TABLE "news" IS 'crude=true';
COMMENT ON TABLE "event" IS 'crude=true';
COMMENT ON TABLE "talk" IS 'crude=true';
    
# --- !Downs

    
