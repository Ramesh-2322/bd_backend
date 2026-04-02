const { body, query } = require("express-validator");

const validStatuses = ["Pending", "Approved", "Rejected"];

const createAppointmentValidation = [
  body("hospital").trim().notEmpty().withMessage("hospital is required"),
  body("date")
    .trim()
    .matches(/^\d{4}-\d{2}-\d{2}$/)
    .withMessage("date must be in YYYY-MM-DD format"),
  body("time")
    .trim()
    .matches(/^([01]\d|2[0-3]):([0-5]\d)$/)
    .withMessage("time must be in HH:mm format"),
  body("status")
    .optional()
    .isIn(validStatuses)
    .withMessage("status must be Pending, Approved, or Rejected"),
];

const appointmentListValidation = [
  query("page").optional().isInt({ min: 1 }).withMessage("page must be >= 1"),
  query("limit").optional().isInt({ min: 1, max: 100 }).withMessage("limit must be 1-100"),
  query("status").optional().isIn(validStatuses).withMessage("Invalid status"),
];

module.exports = { createAppointmentValidation, appointmentListValidation };
