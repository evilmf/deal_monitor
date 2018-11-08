CREATE FUNCTION populate_product_name_ts_vector() RETURNS trigger AS $$
    BEGIN
        NEW.name_tsvector := to_tsvector('simple', NEW.name);
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER populate_product_name_ts_vector_update_trigger BEFORE UPDATE ON products
    FOR EACH ROW 
    WHEN (OLD.name is distinct from NEW.name)
    EXECUTE PROCEDURE populate_product_name_ts_vector();
    
CREATE TRIGGER populate_product_name_ts_vector_insert_trigger BEFORE INSERT ON products
    FOR EACH ROW 
    EXECUTE PROCEDURE populate_product_name_ts_vector();

