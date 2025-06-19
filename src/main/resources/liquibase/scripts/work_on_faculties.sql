--liquibase formatted sql
--changeset alexeychudinov:faculties_1
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'faculty'

CREATE INDEX IF NOT EXISTS get_faculty_name_color_idx ON faculty (name, color);