import {
  Dashboard as DashboardIcon,
  Business as BusinessIcon,
  People as PeopleIcon,
  Description as DescriptionIcon,
  Archive as ArchiveIcon,
  Search as SearchIcon,
  Assessment as AssessmentIcon,
  Storage as StorageIcon,
  Settings as SettingsIcon,
} from "@mui/icons-material";

export const iconMap: Record<string, React.ComponentType> = {
  Dashboard: DashboardIcon,
  Business: BusinessIcon,
  People: PeopleIcon,
  Description: DescriptionIcon,
  Archive: ArchiveIcon,
  Search: SearchIcon,
  Assessment: AssessmentIcon,
  Storage: StorageIcon,
  Settings: SettingsIcon,
};

export const getIconComponent = (iconName: string): React.ComponentType => {
  return iconMap[iconName] || DashboardIcon; // Default to Dashboard if icon not found
}; 