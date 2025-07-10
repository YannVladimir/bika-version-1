import React, { useState } from "react";
import { Box, styled } from "@mui/material";
import Sidebar from "../Sidebar/Sidebar";
import Topbar from "../Topbar/Topbar";
import { Outlet } from "react-router-dom";

const LayoutRoot = styled(Box)(({ theme }) => ({
  display: "flex",
  minHeight: "100vh",
  overflow: "hidden",
  backgroundColor: theme.palette.background.default,
}));

const Main = styled(Box)(({ theme }) => ({
  flexGrow: 1,
  paddingTop: 64, // Height of Topbar
  minHeight: "100vh",
  overflow: "auto",
  transition: "none",
  [theme.breakpoints.down("md")]: {
    paddingTop: 64,
  },
}));

const ContentWrapper = styled(Box)({
  padding: 24,
  width: "100%",
  boxSizing: "border-box",
});

const Layout: React.FC = () => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  // Responsive: hide sidebar by default on small screens
  React.useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth < 900) {
        setSidebarOpen(false);
        setSidebarCollapsed(false);
      } else {
        setSidebarOpen(true);
      }
    };
    window.addEventListener("resize", handleResize);
    handleResize();
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const handleSidebarToggle = () => {
    if (window.innerWidth < 900) {
      setSidebarOpen((open) => !open);
    } else {
      setSidebarCollapsed((collapsed) => !collapsed);
    }
  };

  // Calculate sidebar width (used for future responsive features)
  // const sidebarWidth = sidebarOpen
  //   ? sidebarCollapsed && window.innerWidth >= 900
  //     ? 80
  //     : 280
  //   : 0;

  return (
    <LayoutRoot>
      <Topbar onSidebarToggle={handleSidebarToggle} />
      <Sidebar
        open={sidebarOpen}
        collapsed={sidebarCollapsed}
        onClose={() => setSidebarOpen(false)}
      />
      <Main>
        <ContentWrapper>
          <Outlet />
        </ContentWrapper>
      </Main>
    </LayoutRoot>
  );
};

export default Layout;
