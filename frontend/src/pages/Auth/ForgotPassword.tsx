import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  CircularProgress,
  Container,
  Link,
} from '@mui/material';
import { Email as EmailIcon, ArrowBack as ArrowBackIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { passwordResetService } from '../../services/api/passwordResetService';

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [emailSent, setEmailSent] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setMessage('');

    try {
      await passwordResetService.requestPasswordReset({ email });
      setEmailSent(true);
      setMessage('If an account with that email exists, a password reset link has been sent.');
    } catch (err: any) {
      setError(err.response?.data?.message || 'An error occurred. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleBackToLogin = () => {
    navigate('/login');
  };

  return (
    <Container component="main" maxWidth="sm">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          padding: 3,
        }}
      >
        <Card sx={{ width: '100%', maxWidth: 400 }}>
          <CardContent sx={{ p: 4 }}>
            <Box sx={{ textAlign: 'center', mb: 3 }}>
              <EmailIcon sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
              <Typography variant="h4" component="h1" gutterBottom>
                Forgot Password
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Enter your email address and we'll send you a link to reset your password.
              </Typography>
            </Box>

            {!emailSent ? (
              <Box component="form" onSubmit={handleSubmit}>
                <TextField
                  fullWidth
                  id="email"
                  label="Email Address"
                  name="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  autoComplete="email"
                  autoFocus
                  margin="normal"
                  disabled={loading}
                />

                {error && (
                  <Alert severity="error" sx={{ mt: 2 }}>
                    {error}
                  </Alert>
                )}

                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  disabled={loading || !email.trim()}
                  sx={{ mt: 3, mb: 2 }}
                  startIcon={loading ? <CircularProgress size={20} /> : <EmailIcon />}
                >
                  {loading ? 'Sending...' : 'Send Reset Link'}
                </Button>

                <Box sx={{ textAlign: 'center' }}>
                  <Link
                    component="button"
                    type="button"
                    variant="body2"
                    onClick={handleBackToLogin}
                    sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1 }}
                  >
                    <ArrowBackIcon fontSize="small" />
                    Back to Login
                  </Link>
                </Box>
              </Box>
            ) : (
              <Box sx={{ textAlign: 'center' }}>
                {message && (
                  <Alert severity="success" sx={{ mb: 3 }}>
                    {message}
                  </Alert>
                )}

                <Typography variant="body1" sx={{ mb: 3 }}>
                  Please check your email for the password reset link. It may take a few minutes to arrive.
                </Typography>

                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  Didn't receive the email? Check your spam folder or try again with a different email address.
                </Typography>

                <Button
                  variant="outlined"
                  onClick={() => {
                    setEmailSent(false);
                    setEmail('');
                    setMessage('');
                    setError('');
                  }}
                  sx={{ mr: 2 }}
                >
                  Try Again
                </Button>

                <Button
                  variant="contained"
                  onClick={handleBackToLogin}
                  startIcon={<ArrowBackIcon />}
                >
                  Back to Login
                </Button>
              </Box>
            )}
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
};

export default ForgotPassword; 