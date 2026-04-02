const { body, param, query } = require("express-validator");

const validStatuses = ["Pending", "Approved", "Rejected"];

const createRequestValidation = [
  body("patientName").trim().notEmpty().withMessage("patientName is required"),
  body("bloodGroup").trim().notEmpty().withMessage("bloodGroup is required"),
  body("unitsRequired")
    .isInt({ min: 1 })
    .withMessage("unitsRequired must be an integer >= 1"),
  body("hospital").trim().notEmpty().withMessage("hospital is required"),
  body("location").trim().notEmpty().withMessage("location is required"),
  body("contactNumber").trim().notEmpty().withMessage("contactNumber is required"),
];

const updateRequestStatusValidation = [
  param("id").isMongoId().withMessage("Invalid request id"),
  body("status")
    .isIn(validStatuses)
    .withMessage("status must be Pending, Approved, or Rejected"),
];

const requestIdValidation = [param("id").isMongoId().withMessage("Invalid request id")];

const requestListValidation = [
  query("page").optional().isInt({ min: 1 }).withMessage("page must be >= 1"),
  query("limit").optional().isInt({ min: 1, max: 100 }).withMessage("limit must be 1-100"),
  query("status").optional().isIn(validStatuses).withMessage("Invalid status"),
];

module.exports = {
  createRequestValidation,
  updateRequestStatusValidation,
  requestIdValidation,
  requestListValidation,
};
