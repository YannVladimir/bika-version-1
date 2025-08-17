import React, { useState } from "react";
import {
  Box,
  Card,
  TextField,
  Button,
  Typography,
  Link,
  styled,
  IconButton,
  InputAdornment,
  Alert,
  CircularProgress,
} from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

const LoginRoot = styled(Box)({
  minHeight: "100vh",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  padding: "20px",
  backgroundImage: "url('/archive-bg.webp')",
  backgroundSize: "cover",
  backgroundPosition: "center",
  backgroundRepeat: "no-repeat",
  position: "relative",
  "&::before": {
    content: '""',
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0, 0, 0, 0.4)", // Dark overlay for better text readability
    zIndex: 1,
  },
});

const LoginCard = styled(Card)(({ theme }) => ({
  padding: theme.spacing(4),
  width: "100%",
  maxWidth: 400,
  borderRadius: 16,
  boxShadow: "0 8px 32px rgba(0, 0, 0, 0.3)",
  backgroundColor: "rgba(255, 255, 255, 0.95)", // Semi-transparent white background
  backdropFilter: "blur(10px)", // Glass effect
  position: "relative",
  zIndex: 2,
}));

const Logo = styled("img")({
  width: 80,
  height: 80,
  marginBottom: 24,
  borderRadius: "8px", // Slight rounded corners for the logo
});

const Form = styled("form")({
  display: "flex",
  flexDirection: "column",
  gap: 24,
});

const ForgotPassword = styled(Link)({
  textAlign: "right",
  textDecoration: "none",
  marginTop: -16,
  marginBottom: 16,
  "&:hover": {
    textDecoration: "underline",
  },
});

const Login: React.FC = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Please fill in all fields");
      return;
    }

    setIsLoading(true);
    setError("");

    try {
      await login(email, password);
      navigate("/dashboard");
    } catch (err: any) {
      setError(
        err.response?.data?.message || "Login failed. Please try again."
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <LoginRoot>
      <LoginCard>
        <Box display="flex" flexDirection="column" alignItems="center">
          <Logo src="/logo.ico" alt="Bika Logo" />
          <Typography variant="h5" fontWeight={600} mb={3}>
            Welcome back
          </Typography>
          <Typography variant="body2" color="text.secondary" mb={4}>
            Enter your credentials to access your account
          </Typography>
        </Box>

        <Form onSubmit={handleSubmit}>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <TextField
            fullWidth
            label="Email address"
            variant="outlined"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            disabled={isLoading}
          />

          <TextField
            fullWidth
            label="Password"
            variant="outlined"
            type={showPassword ? "text" : "password"}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            disabled={isLoading}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton
                    onClick={() => setShowPassword(!showPassword)}
                    edge="end"
                    disabled={isLoading}
                  >
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />

          <ForgotPassword
            href="#"
            variant="body2"
            color="primary"
            onClick={(e) => {
              e.preventDefault();
              // Handle forgot password
            }}
          >
            Forgot password?
          </ForgotPassword>

          <Button
            type="submit"
            variant="contained"
            size="large"
            fullWidth
            disabled={isLoading}
            sx={{
              height: 48,
              background: `linear-gradient(90deg, ${tokens.primary.main}, ${tokens.secondary.main})`,
              "&:hover": {
                background: `linear-gradient(90deg, ${tokens.primary.dark}, ${tokens.secondary.dark})`,
              },
            }}
          >
            {isLoading ? <CircularProgress size={24} /> : "Sign in"}
          </Button>
        </Form>
      </LoginCard>
    </LoginRoot>
  );
};

export default Login;
