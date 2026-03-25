-- TẠO DATABASE
CREATE DATABASE BlogCNTT COLLATE SQL_Latin1_General_CP1_CI_AS;
GO
USE BlogCNTT;
GO

-- USERS
CREATE TABLE users (
    id BIGINT IDENTITY PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at DATETIME DEFAULT GETDATE()
);

-- POSTS
CREATE TABLE post (
    id BIGINT IDENTITY PRIMARY KEY,
    title NVARCHAR(MAX),
    content NVARCHAR(MAX),
    image_url NVARCHAR(500),
    status NVARCHAR(50) DEFAULT 'APPROVED',
    likes INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- COMMENTS
CREATE TABLE comment (
    id BIGINT IDENTITY PRIMARY KEY,
    content NVARCHAR(MAX) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION
);

-- QUESTIONS
CREATE TABLE question (
    id BIGINT IDENTITY PRIMARY KEY,
    title NVARCHAR(MAX),
    content NVARCHAR(MAX),
    status NVARCHAR(50) DEFAULT 'APPROVED',
    approved BIT DEFAULT 1, 
    created_at DATETIME DEFAULT GETDATE(),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ANSWERS
CREATE TABLE answer (
    id BIGINT IDENTITY PRIMARY KEY,
    content NVARCHAR(MAX) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION
);

-- DOCUMENTS
CREATE TABLE documents (
    id BIGINT IDENTITY PRIMARY KEY,
    title NVARCHAR(255),
    link NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE()
);

-- LIKE POST
CREATE TABLE like_post (
    id BIGINT IDENTITY PRIMARY KEY,
    post_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (post_id) REFERENCES post(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- DATA MẪU
INSERT INTO users(username, password, role) 
VALUES (N'admin', N'123', 'ADMIN');

INSERT INTO documents(title, link)
VALUES
(N'Tài liệu học Java từ cơ bản đến nâng cao', 'https://www.w3schools.com/java/'),
(N'Hướng dẫn HTML CSS đầy đủ cho người mới', 'https://www.w3schools.com/html/'),
(N'Tài liệu JavaScript từ A-Z', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript'),
(N'Khóa học miễn phí về AI và Machine Learning', 'https://www.coursera.org/learn/machine-learning'),
(N'Tài liệu Spring Boot chính thức', 'https://spring.io/projects/spring-boot');

select * from comment;