ALTER TABLE restaurants
    ADD COLUMN owner_id UUID;

CREATE INDEX idx_restaurants_owner_id
    ON restaurants (owner_id);
