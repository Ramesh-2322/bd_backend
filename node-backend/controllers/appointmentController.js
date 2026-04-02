const Appointment = require("../models/Appointment");
const asyncHandler = require("../middleware/asyncHandler");

const createAppointment = asyncHandler(async (req, res) => {
  const appointment = await Appointment.create({
    userId: req.user.id,
    hospital: req.body.hospital,
    date: req.body.date,
    time: req.body.time,
    status: req.body.status || "Pending",
  });

  res.status(201).json({
    message: "Appointment created",
    data: appointment,
  });
});

const getAppointments = asyncHandler(async (req, res) => {
  const page = Number(req.query.page) || 1;
  const limit = Number(req.query.limit) || 10;
  const skip = (page - 1) * limit;

  const filter = { userId: req.user.id };
  if (req.query.status) {
    filter.status = req.query.status;
  }

  const [items, totalItems] = await Promise.all([
    Appointment.find(filter)
      .populate("userId", "name email")
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(limit),
    Appointment.countDocuments(filter),
  ]);

  res.json({
    data: items,
    pagination: {
      page,
      limit,
      totalItems,
      totalPages: Math.ceil(totalItems / limit),
    },
  });
});

module.exports = { createAppointment, getAppointments };
