import React from "react";
import {
  Box,
  Card,
  CardContent,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Avatar,
  styled,
} from "@mui/material";
import {
  Description as DocumentIcon,
  CloudUpload as UploadIcon,
  Storage as StorageIcon,
  Group as UsersIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";

const StatsCard = styled(Card)(({ theme }) => ({
  height: "100%",
  background: theme.palette.background.paper,
  borderRadius: 16,
  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.05)",
  transition: "transform 0.2s ease-in-out",
  "&:hover": {
    transform: "translateY(-4px)",
  },
}));

const StatValue = styled(Typography)({
  fontSize: "2rem",
  fontWeight: 600,
  marginBottom: 8,
});

const ActivityCard = styled(Card)(({ theme }) => ({
  height: "100%",
  background: theme.palette.background.paper,
  borderRadius: 16,
  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.05)",
}));

const GradientBox = styled(Box)({
  background: `linear-gradient(135deg, ${tokens.primary.main}, ${tokens.secondary.main})`,
  borderRadius: "50%",
  padding: 8,
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  color: "white",
});

const Dashboard: React.FC = () => {
  const stats = [
    {
      title: "Total Documents",
      value: "1,234",
      icon: <DocumentIcon />,
    },
    {
      title: "Storage Used",
      value: "45.2 GB",
      icon: <StorageIcon />,
    },
    {
      title: "Active Users",
      value: "56",
      icon: <UsersIcon />,
    },
    {
      title: "Uploads Today",
      value: "28",
      icon: <UploadIcon />,
    },
  ];

  const recentActivities = [
    {
      user: "John Doe",
      action: "uploaded",
      document: "Q4 Financial Report.pdf",
      time: "5 minutes ago",
    },
    {
      user: "Sarah Smith",
      action: "modified",
      document: "Project Proposal.docx",
      time: "2 hours ago",
    },
    {
      user: "Mike Johnson",
      action: "archived",
      document: "Client Contract.pdf",
      time: "4 hours ago",
    },
    {
      user: "Emily Brown",
      action: "shared",
      document: "Marketing Plan.pptx",
      time: "6 hours ago",
    },
  ];

  return (
    <Box
      width="100%"
      boxSizing="border-box"
      display="flex"
      flexDirection="column"
      alignItems="flex-start"
    >
      <Typography variant="h4" fontWeight={600} mb={4} align="left">
        Dashboard
      </Typography>
      <Box display="flex" flexWrap="wrap" gap={3} mb={3} width="100%">
        {stats.map((stat) => (
          <Box key={stat.title} flex="1 1 220px" minWidth={220} maxWidth={340}>
            <StatsCard>
              <CardContent>
                <Box display="flex" alignItems="center" mb={2}>
                  <GradientBox>{stat.icon}</GradientBox>
                </Box>
                <StatValue>{stat.value}</StatValue>
                <Typography color="text.secondary">{stat.title}</Typography>
              </CardContent>
            </StatsCard>
          </Box>
        ))}
      </Box>
      <ActivityCard sx={{ width: "100%" }}>
        <CardContent>
          <Typography variant="h6" fontWeight={500} mb={2} align="left">
            Recent Activities
          </Typography>
          <List>
            {recentActivities.map((activity, index) => (
              <ListItem
                key={index}
                sx={{
                  borderBottom:
                    index < recentActivities.length - 1
                      ? "1px solid rgba(0, 0, 0, 0.08)"
                      : "none",
                  py: 2,
                }}
              >
                <ListItemIcon>
                  <Avatar
                    sx={{
                      width: 40,
                      height: 40,
                      bgcolor: `${tokens.primary.main}`,
                    }}
                  >
                    {activity.user.charAt(0)}
                  </Avatar>
                </ListItemIcon>
                <ListItemText
                  primary={
                    <Typography variant="body1">
                      <strong>{activity.user}</strong> {activity.action}{" "}
                      <strong>{activity.document}</strong>
                    </Typography>
                  }
                  secondary={activity.time}
                />
              </ListItem>
            ))}
          </List>
        </CardContent>
      </ActivityCard>
    </Box>
  );
};

export default Dashboard;
