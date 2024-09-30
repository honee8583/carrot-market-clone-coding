ALTER TABLE keywords CHANGE minPrice min_price INT;
ALTER TABLE keywords CHANGE maxPrice max_price INT;
ALTER TABLE keywords DROP FOREIGN KEY keywords_ibfk_2;
ALTER TABLE keywords MODIFY category_id bigint NULL;
