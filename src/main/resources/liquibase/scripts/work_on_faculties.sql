--liquibase formatted sql
--changeset alexeychudinov:faculties_1
CREATE INDEX IF NOT EXISTS get_faculty_name_color_idx ON faculty (name, color);