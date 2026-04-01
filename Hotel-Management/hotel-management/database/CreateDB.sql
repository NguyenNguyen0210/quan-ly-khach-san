CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;

CREATE TABLE employee (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(100) NOT NULL,
    salary DOUBLE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id)
);

CREATE TABLE customer (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255),
    id_card VARCHAR(50),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id)
);

CREATE TABLE room_type (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price_per_night DOUBLE NOT NULL,
    description VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id)
);

CREATE TABLE room (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_number VARCHAR(50) NOT NULL UNIQUE,
    room_type_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_room_room_type
        FOREIGN KEY (room_type_id) REFERENCES room_type(id)
);

CREATE TABLE service (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id)
);

CREATE TABLE booking (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    created_by_employee_id BIGINT NULL,
    checked_in_by_employee_id BIGINT NULL,
    checked_out_by_employee_id BIGINT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_booking_customer
        FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_booking_room
        FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_booking_created_by
        FOREIGN KEY (created_by_employee_id) REFERENCES employee(id),
    CONSTRAINT fk_booking_checked_in_by
        FOREIGN KEY (checked_in_by_employee_id) REFERENCES employee(id),
    CONSTRAINT fk_booking_checked_out_by
        FOREIGN KEY (checked_out_by_employee_id) REFERENCES employee(id)
);

CREATE TABLE service_usage (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    added_by_employee_id BIGINT NULL,
    quantity INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_service_usage_service
        FOREIGN KEY (service_id) REFERENCES service(id),
    CONSTRAINT fk_service_usage_booking
        FOREIGN KEY (booking_id) REFERENCES booking(id),
    CONSTRAINT fk_service_usage_employee
        FOREIGN KEY (added_by_employee_id) REFERENCES employee(id)
);

CREATE TABLE invoice (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    total_amount DOUBLE NOT NULL,
    created_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    settled_date DATETIME NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_invoice_booking
        FOREIGN KEY (booking_id) REFERENCES booking(id)
);

CREATE TABLE invoice_detail (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    service_id BIGINT NULL,
    description VARCHAR(500),
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    amount DOUBLE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_invoice_detail_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    CONSTRAINT fk_invoice_detail_service
        FOREIGN KEY (service_id) REFERENCES service(id)
);

CREATE TABLE payment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    processed_by_employee_id BIGINT NULL,
    amount DOUBLE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_payment_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    CONSTRAINT fk_payment_employee
        FOREIGN KEY (processed_by_employee_id) REFERENCES employee(id)
);
