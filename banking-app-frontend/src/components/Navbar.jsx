import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box, useTheme } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import HomeIcon from '@mui/icons-material/Home';

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useTheme();

  const isAuthPage = ['/login', '/register', '/admin/login', '/forgot-password'].includes(location.pathname);
  const isUserPage = !isAuthPage && !location.pathname.startsWith('/admin');

  return (
    <AppBar position="static" sx={{ backgroundColor: theme.palette.primary.main }}>
      <Toolbar>
        <Box sx={{ display: 'flex', alignItems: 'center', flexGrow: 1 }}>
          <AccountBalanceIcon sx={{ mr: 1 }} />
          <Typography variant="h6" component="div" sx={{ cursor: 'pointer' }} onClick={() => navigate('/')}>
            Banking App
          </Typography>
        </Box>

        {isAuthPage ? (
          <Button 
            color="inherit" 
            startIcon={<HomeIcon />}
            onClick={() => navigate('/')}
          >
            Home
          </Button>
        ) : isUserPage ? (
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button 
              color="inherit" 
              onClick={() => navigate('/dashboard')}
            >
              Dashboard
            </Button>
            <Button 
              color="inherit" 
              onClick={() => navigate('/beneficiaries')}
            >
              Beneficiaries
            </Button>
            <Button 
              color="inherit" 
              onClick={() => navigate('/customer-details')}
            >
              Profile
            </Button>
            <Button 
              color="inherit" 
              onClick={() => {
                localStorage.removeItem('token');
                navigate('/');
              }}
            >
              Logout
            </Button>
          </Box>
        ) : (
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button 
              color="inherit" 
              onClick={() => navigate('/login')}
            >
              Login
            </Button>
            <Button 
              color="inherit" 
              onClick={() => navigate('/register')}
            >
              Register
            </Button>
            <Button 
              color="inherit" 
              onClick={() => navigate('/admin/login')}
            >
              Admin Login
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Navbar; 