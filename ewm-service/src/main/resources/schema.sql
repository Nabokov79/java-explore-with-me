CREATE TABLE IF NOT EXISTS USERS
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS CATEGORIES
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN,
    title  VARCHAR,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         VARCHAR(2000),
    category_id        BIGINT,
    initiator_id       BIGINT,
    description        VARCHAR(7000),
    event_date          TIMESTAMP,
    created_on         TIMESTAMP,
    published_on       TIMESTAMP,
    paid               BOOLEAN,
    participant_limit  INTEGER,
    request_moderation BOOLEAN,
    title              VARCHAR(120),
    lat                FLOAT,
    lon                FLOAT,
    state              VARCHAR,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT FK_EVENT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT FK_EVENT_ON_USER FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created      TIMESTAMP,
    event_id     BIGINT,
    requester_id BIGINT,
    status       VARCHAR,
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT FK_REQUEST_ON_REQUESTER FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT FK_REQUEST_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS COMPILATIONS_EVENTS
(
    compilation_id BIGINT,
    event_id       BIGINT,
    CONSTRAINT FK_STUDENT_ID FOREIGN KEY (compilation_id) REFERENCES compilations (id)
        MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_UNIVERSITY_ID FOREIGN KEY (event_id) REFERENCES events (id)
)
