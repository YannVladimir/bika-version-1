import React from "react";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { ThemeProvider } from "./theme/ThemeProvider";
import { CssBaseline } from "@mui/material";
import Dashboard from "./pages/Dashboard/Dashboard";
import Companies from "./pages/Companies/Companies";
import Users from "./pages/Users/Users";
import DocumentTypes from "./pages/DocumentTypes/DocumentTypes";
import ArchivalPage from "./pages/Archival/ArchivalPage";
import Reports from "./pages/Reports/ReportsPage";
import Drive from "./pages/Drive/Drive";
import Settings from "./pages/Settings/Settings";
import Profile from "./pages/Profile/Profile";
import Login from "./pages/Login/Login";
import Layout from "./components/Layout/Layout";
import RetrievalPage from "./pages/Retrieval/RetrievalPage";
import ProtectedRoute from "./components/ProtectedRoute";
import RoleBasedRoute from "./components/RoleBasedRoute";
import { AuthProvider } from "./context/AuthContext";
import { QueryProvider } from "./hooks/useApiQuery";
import { USER_ROLES } from "./constants/roles";

const router = createBrowserRouter(
  [
    {
      path: "/login",
      element: <Login />,
    },
    {
      path: "/",
      element: (
        <ProtectedRoute>
          <Layout />
        </ProtectedRoute>
      ),
      children: [
        {
          path: "",
          element: <Dashboard />,
        },
        {
          path: "dashboard",
          element: <Dashboard />,
        },
        {
          path: "companies",
          element: (
            <RoleBasedRoute allowedRoles={[USER_ROLES.SUPER_ADMIN]}>
              <Companies />
            </RoleBasedRoute>
          ),
        },
        {
          path: "users",
          element: (
            <RoleBasedRoute allowedRoles={[USER_ROLES.SUPER_ADMIN, USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER]}>
              <Users />
            </RoleBasedRoute>
          ),
        },
        {
          path: "document-types",
          element: (
            <RoleBasedRoute allowedRoles={[USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER]}>
              <DocumentTypes />
            </RoleBasedRoute>
          ),
        },
        {
          path: "archival",
          element: (
            <RoleBasedRoute allowedRoles={[USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER]}>
              <ArchivalPage />
            </RoleBasedRoute>
          ),
        },
        {
          path: "retrieval",
          element: (
            <RoleBasedRoute allowedRoles={[USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER]}>
              <RetrievalPage />
            </RoleBasedRoute>
          ),
        },
        {
          path: "reports",
          element: (
            <RoleBasedRoute allowedRoles={[USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER]}>
              <Reports />
            </RoleBasedRoute>
          ),
        },
        {
          path: "drive",
          element: (
            <RoleBasedRoute allowedRoles={[USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER]}>
              <Drive />
            </RoleBasedRoute>
          ),
        },
        {
          path: "settings",
          element: (
            <RoleBasedRoute allowedRoles={[USER_ROLES.SUPER_ADMIN, USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER]}>
              <Settings />
            </RoleBasedRoute>
          ),
        },
        {
          path: "profile",
          element: <Profile />,
        },
      ],
    },
  ],
  {
    future: {
      v7_relativeSplatPath: true,
    },
  }
);

function App() {
  return (
    <QueryProvider>
      <AuthProvider>
        <ThemeProvider>
          <CssBaseline />
          <RouterProvider router={router} />
        </ThemeProvider>
      </AuthProvider>
    </QueryProvider>
  );
}

export default App;
