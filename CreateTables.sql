
CREATE TABLE Books
(   b_BookId NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1) NOT NULL PRIMARY KEY,
    b_Title   VARCHAR(100) NOT NULL,
    b_Author  VARCHAR(50),
    b_Publisher VARCHAR(50),
    b_Quantity INTEGER
);

CREATE TABLE Users
(   u_UserId  NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1) NOT NULL PRIMARY KEY,
    u_Username   VARCHAR(100) NOT NULL,
    u_Password  VARCHAR(50),
    u_Role VARCHAR(50)
);

CREATE TABLE Reservations
(   r_ReservationId  NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1) NOT NULL PRIMARY KEY,
    r_BookId NUMBER NOT NULL,
    r_UserId NUMBER NOT NULL,
    r_StartDate DATE,
    r_EndDate DATE,
    CONSTRAINT fk_book FOREIGN KEY (r_BookId) REFERENCES BOOKS(b_BookId),
    CONSTRAINT fk_user FOREIGN KEY (r_UserId) REFERENCES Users(u_UserId)
);