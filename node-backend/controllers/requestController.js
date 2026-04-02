const Request = require("../models/Request");
const asyncHandler = require("../middleware/asyncHandler");

const createRequest = asyncHandler(async (req, res) => {
  const payload = {
    ...req.body,
    createdBy: req.user.id,
  };

  const request = await Request.create(payload);

  res.status(201).json({
    message: "Blood request created",
    data: request,
  });
});

const getRequests = asyncHandler(async (req, res) => {
  const page = Number(req.query.page) || 1;
  const limit = Number(req.query.limit) || 10;
  const skip = (page - 1) * limit;

  const filter = {};
  if (req.query.status) {
    filter.status = req.query.status;
  }

  const [items, totalItems] = await Promise.all([
    Request.find(filter)
      .populate("createdBy", "name email")
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(limit),
    Request.countDocuments(filter),
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

const getRequestById = asyncHandler(async (req, res, next) => {
  const request = await Request.findById(req.params.id).populate("createdBy", "name email");

  if (!request) {
    const error = new Error("Request not found");
    error.statusCode = 404;
    return next(error);
  }

  return res.json({ data: request });
});

const updateRequestStatus = asyncHandler(async (req, res, next) => {
  const { status } = req.body;

  const request = await Request.findById(req.params.id);

  if (!request) {
    const error = new Error("Request not found");
    error.statusCode = 404;
    return next(error);
  }

  request.status = status;
  await request.save();

  return res.json({
    message: "Request status updated",
    data: request,
  });
});

module.exports = {
  createRequest,
  getRequests,
  getRequestById,
  updateRequestStatus,
};
