import apiClient from './client';

export interface PasswordResetRequest {
  email: string;
}

export interface PasswordResetData {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

export interface TokenValidationResponse {
  valid: boolean;
  email?: string;
  message: string;
}

export interface PasswordResetResponse {
  message: string;
  status: string;
}

export const passwordResetService = {
  async requestPasswordReset(data: PasswordResetRequest): Promise<PasswordResetResponse> {
    try {
      const response = await apiClient.post<PasswordResetResponse>('/auth/password/reset-request', data);
      return response.data;
    } catch (error) {
      console.error('Failed to request password reset:', error);
      throw error;
    }
  },

  async validateResetToken(token: string): Promise<TokenValidationResponse> {
    try {
      const response = await apiClient.get<TokenValidationResponse>(`/auth/password/validate-token?token=${encodeURIComponent(token)}`);
      return response.data;
    } catch (error) {
      console.error('Failed to validate reset token:', error);
      throw error;
    }
  },

  async resetPassword(data: PasswordResetData): Promise<PasswordResetResponse> {
    try {
      const response = await apiClient.post<PasswordResetResponse>('/auth/password/reset', data);
      return response.data;
    } catch (error) {
      console.error('Failed to reset password:', error);
      throw error;
    }
  },
}; 