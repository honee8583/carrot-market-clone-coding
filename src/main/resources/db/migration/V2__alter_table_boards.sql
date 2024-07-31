ALTER TABLE boards
    ADD COLUMN category_id BIGINT;

ALTER TABLE boards
    ADD CONSTRAINT fk_category
        FOREIGN KEY (category_id) REFERENCES categories (id);

drop table board_categories;

alter table members
    change column is_authenticated is_town_authenticated TINYINT(1);

alter table members
    add column role VARCHAR(10);

insert into categories
    values (1, '디지털기기', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (2, '생활가전', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (3, '가구/인테리어', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (4, '생활/주방', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (5, '유아동', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (6, '유아도서', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (7, '여성의류', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (8, '여성잡화', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (9, '남성패션/잡화', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (10, '뷰티/미용', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (11, '스포츠/레저', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (12, '취미/게임/음반', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (13, '도서', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (14, '티켓/교환권', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (15, '가공식품', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (16, '건강기능식품', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (17, '반려동물용품', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (18, '식물', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (19, '기타 중고물품', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (20, '삽니다', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
