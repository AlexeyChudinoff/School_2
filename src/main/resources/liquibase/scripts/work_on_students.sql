--liquibase formatted sql
--changeset alexeychudinov:student_1
CREATE INDEX IF NOT EXISTS get_name_student_idx ON student (name);