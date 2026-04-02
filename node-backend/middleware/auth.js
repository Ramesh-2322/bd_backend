const jwt = require("jsonwebtoken");

function auth(req, _res, next) {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    const error = new Error("Access denied. No token provided.");
    error.statusCode = 401;
    return next(error);
  }

  const token = authHeader.split(" ")[1];

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    return next();
  } catch (_error) {
    const error = new Error("Invalid or expired token.");
    error.statusCode = 401;
    return next(error);
  }
}

module.exports = auth;
