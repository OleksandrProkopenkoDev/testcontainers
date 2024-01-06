CREATE TABLE IF NOT EXISTS post
(
    id      INT          NOT NULL,
    user_id INT          NOT NULL,
    title   varchar(255) NOT NULL,
    body    text         not null,
    version int,
    PRIMARY KEY (id)
);