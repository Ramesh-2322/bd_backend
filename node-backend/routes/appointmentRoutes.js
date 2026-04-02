const express = require("express");
const auth = require("../middleware/auth");
const validate = require("../middleware/validate");
const {
  createAppointment,
  getAppointments,
} = require("../controllers/appointmentController");
const {
  createAppointmentValidation,
  appointmentListValidation,
} = require("../validators/appointmentValidator");

const router = express.Router();

router.post("/", auth, createAppointmentValidation, validate, createAppointment);
router.get("/", auth, appointmentListValidation, validate, getAppointments);

module.exports = router;
