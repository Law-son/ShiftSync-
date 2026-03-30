-- V1__init_schema.sql

-- LOCATIONS
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    max_headcount_per_shift INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DEPARTMENTS
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    location_id INT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- USERS (auth + role only)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- EMPLOYEE, MANAGER, HR_ADMIN
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- EMPLOYEES (full profile, 1:1 with users)
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    employment_type VARCHAR(50), -- FULL_TIME, PART_TIME, CONTRACT
    department_id INT REFERENCES departments(id) ON DELETE SET NULL,
    location_id INT REFERENCES locations(id) ON DELETE SET NULL,
    skills_certifications TEXT[],
    contracted_weekly_hours NUMERIC(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- MANAGER LOCATIONS (Join table for managers and their assigned locations)
CREATE TABLE manager_locations (
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    location_id INT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, location_id)
);

-- RECURRING AVAILABILITIES
CREATE TABLE recurring_availabilities (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day_of_week INT NOT NULL, -- 1 = Monday, 7 = Sunday
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- UNAVAILABILITY BLOCKS (One-off blocks)
CREATE TABLE unavailability_blocks (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LEAVE REQUESTS
CREATE TABLE leave_requests (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    leave_type VARCHAR(50) NOT NULL, -- ANNUAL, SICK, UNPAID
    status VARCHAR(50) NOT NULL, -- PENDING, APPROVED, REJECTED, CANCELLED
    reason TEXT,
    hr_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SHIFTS
CREATE TABLE shifts (
    id SERIAL PRIMARY KEY,
    location_id INT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    department_id INT NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    shift_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    required_skill VARCHAR(255),
    minimum_headcount INT DEFAULT 1,
    is_cancelled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SHIFT ASSIGNMENTS
CREATE TABLE shift_assignments (
    id SERIAL PRIMARY KEY,
    shift_id INT NOT NULL REFERENCES shifts(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (shift_id, user_id) -- Prevents assigning the same user to the same shift multiple times
);

-- SWAP REQUESTS
CREATE TABLE swap_requests (
    id SERIAL PRIMARY KEY,
    requesting_user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    source_shift_assignment_id INT NOT NULL REFERENCES shift_assignments(id) ON DELETE CASCADE,
    target_shift_assignment_id INT REFERENCES shift_assignments(id) ON DELETE SET NULL,
    status VARCHAR(50) NOT NULL, -- PENDING_MANAGER_APPROVAL, APPROVED, REJECTED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NOTIFICATIONS
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AUDIT LOGS
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE
    actor_user_id INT, -- Nullable because system actions might not have an actor
    actor_role VARCHAR(50),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    previous_state JSONB,
    new_state JSONB
);
