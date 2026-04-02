const Report = require("../models/Report");
const asyncHandler = require("../middleware/asyncHandler");

const uploadReport = asyncHandler(async (req, res, next) => {
  if (!req.file) {
    const error = new Error("No file uploaded");
    error.statusCode = 400;
    return next(error);
  }

  const fileUrl = `${req.protocol}://${req.get("host")}/uploads/${req.file.filename}`;

  const report = await Report.create({
    userId: req.user.id,
    fileUrl,
  });

  return res.status(201).json({
    message: "Report uploaded successfully",
    data: report,
  });
});

module.exports = { uploadReport };
