USE hotel_management;

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO employee (id, full_name, username, password, role, salary, created_at, updated_at) VALUES
(1, 'System Manager', 'admin', 'admin123', 'MANAGER', 18000000, NOW(), NOW()),
(2, 'Nguyen Le Tan', 'reception1', '123456', 'RECEPTIONIST', 9000000, NOW(), NOW()),
(3, 'Tran Le Tan', 'reception2', '123456', 'RECEPTIONIST', 8500000, NOW(), NOW()),
(4, 'Pham Service', 'service1', '123456', 'SERVICE_STAFF', 7500000, NOW(), NOW()),
(5, 'Hoang Service', 'service2', '123456', 'SERVICE_STAFF', 7300000, NOW(), NOW());

INSERT INTO customer (id, full_name, phone, email, id_card, created_at, updated_at) VALUES
(1, 'Nguyen Van An', '0901000001', 'an@gmail.com', '079201000001', NOW(), NOW()),
(2, 'Tran Thi Binh', '0901000002', 'binh@gmail.com', '079201000002', NOW(), NOW()),
(3, 'Le Quoc Cuong', '0901000003', 'cuong@gmail.com', '079201000003', NOW(), NOW()),
(4, 'Pham Thu Dung', '0901000004', 'dung@gmail.com', '079201000004', NOW(), NOW()),
(5, 'Hoang Minh Duc', '0901000005', 'duc@gmail.com', '079201000005', NOW(), NOW());

INSERT INTO room_type (id, name, price_per_night, description, created_at, updated_at) VALUES
(1, 'Standard', 450000, 'Standard room for 2 guests', NOW(), NOW()),
(2, 'Deluxe', 700000, 'Deluxe room with city view', NOW(), NOW()),
(3, 'Suite', 1200000, 'Suite room with premium amenities', NOW(), NOW());

INSERT INTO room (id, room_number, room_type_id, status, created_at, updated_at) VALUES
(1, '101', 1, 'AVAILABLE', NOW(), NOW()),
(2, '102', 1, 'AVAILABLE', NOW(), NOW()),
(3, '103', 1, 'AVAILABLE', NOW(), NOW()),
(4, '201', 2, 'AVAILABLE', NOW(), NOW()),
(5, '202', 2, 'OCCUPIED', NOW(), NOW()),
(6, '203', 2, 'AVAILABLE', NOW(), NOW()),
(7, '301', 3, 'OCCUPIED', NOW(), NOW()),
(8, '302', 3, 'AVAILABLE', NOW(), NOW());

INSERT INTO service (id, name, price, quantity, created_at, updated_at) VALUES
(1, 'Mini Bar', 120000, 50, NOW(), NOW()),
(2, 'Laundry', 50000, 100, NOW(), NOW()),
(3, 'Breakfast', 80000, 80, NOW(), NOW()),
(4, 'Airport Pickup', 250000, 20, NOW(), NOW()),
(5, 'Spa', 400000, 15, NOW(), NOW());

INSERT INTO booking (
    id, customer_id, room_id, created_by_employee_id, checked_in_by_employee_id, checked_out_by_employee_id,
    check_in_date, check_out_date, status, created_at, updated_at
) VALUES
(1, 1, 1, 2, NULL, NULL, '2026-04-05', '2026-04-07', 'BOOKED', NOW(), NOW()),
(2, 2, 5, 2, 2, NULL, '2026-03-30', '2026-04-02', 'CHECKED_IN', NOW(), NOW()),
(3, 3, 7, 3, 3, NULL, '2026-03-31', '2026-04-03', 'CHECKED_IN', NOW(), NOW()),
(4, 4, 2, 2, 2, 2, '2026-03-25', '2026-03-27', 'CHECKED_OUT', NOW(), NOW());

INSERT INTO service_usage (
    id, service_id, booking_id, added_by_employee_id, quantity, created_at, updated_at
) VALUES
(1, 3, 2, 4, 2, NOW(), NOW()),
(2, 2, 2, 4, 3, NOW(), NOW()),
(3, 1, 3, 5, 1, NOW(), NOW()),
(4, 5, 3, 5, 1, NOW(), NOW()),
(5, 4, 4, 4, 1, NOW(), NOW());

INSERT INTO invoice (
    id, booking_id, total_amount, created_date, status, settled_date, created_at, updated_at
) VALUES
(1, 2, 1510000, NOW(), 'PARTIALLY_PAID', NULL, NOW(), NOW()),
(2, 3, 1720000, NOW(), 'ISSUED', NULL, NOW(), NOW()),
(3, 4, 700000, DATE_SUB(NOW(), INTERVAL 4 DAY), 'CLOSED', DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), NOW());

INSERT INTO invoice_detail (
    id, invoice_id, service_id, description, quantity, unit_price, amount, created_at, updated_at
) VALUES
(1, 1, NULL, 'Room charge - Deluxe', 2, 700000, 1400000, NOW(), NOW()),
(2, 1, 3, 'Breakfast', 2, 80000, 160000, NOW(), NOW()),
(3, 1, 2, 'Laundry', 3, 50000, 150000, NOW(), NOW()),
(4, 2, NULL, 'Room charge - Suite', 1, 1200000, 1200000, NOW(), NOW()),
(5, 2, 1, 'Mini Bar', 1, 120000, 120000, NOW(), NOW()),
(6, 2, 5, 'Spa', 1, 400000, 400000, NOW(), NOW()),
(7, 3, NULL, 'Room charge - Standard', 1, 450000, 450000, NOW(), NOW()),
(8, 3, 4, 'Airport Pickup', 1, 250000, 250000, NOW(), NOW());

INSERT INTO payment (
    id, invoice_id, processed_by_employee_id, amount, payment_method, payment_date, created_at, updated_at
) VALUES
(1, 1, 2, 800000, 'CASH', NOW(), NOW(), NOW()),
(2, 3, 2, 700000, 'CASH', DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
