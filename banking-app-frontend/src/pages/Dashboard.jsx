import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { authAPI } from '../services/api';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardHeader,
  Button,
  CircularProgress,
  Alert,
  Container
} from '@mui/material';
import {
  AccountBalance as AccountBalanceIcon,
  Send as SendIcon,
  Receipt as ReceiptIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
  const [balance, setBalance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user } = useSelector((state) => state.auth);
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState(null);

  useEffect(() => {
    const fetchBalance = async () => {
      try {
        const response = await authAPI.getAccountBalance();
        setBalance(response.data.balances);
      } catch (error) {
        setError('Failed to fetch account balance');
      } finally {
        setLoading(false);
      }
    };

    const fetchUserInfo = async () => {
      try {
        const response = await authAPI.getCurrentUser();
        setUserInfo(response.data);
      } catch (error) {
        // Optionally handle error
      }
    };

    fetchBalance();
    fetchUserInfo();
  }, []);

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Dashboard
        </Typography>
        <Typography variant="subtitle1" color="text.secondary">
          Welcome back, {userInfo?.firstName ? userInfo.firstName : 'User'}!
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {loading ? (
        <Box display="flex" justifyContent="center" my={4}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={3}>
          {balance &&
            Object.entries(balance).map(([accountNumber, amount]) => (
              <Grid item xs={12} md={6} lg={4} key={accountNumber}>
                <Card>
                  <CardHeader
                    avatar={<AccountBalanceIcon color="primary" />}
                    title="Account Balance"
                    subheader={accountNumber}
                  />
                  <CardContent>
                    <Typography variant="h4" component="div" gutterBottom>
                      ${amount.toFixed(2)}
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
        </Grid>
      )}

      <Box sx={{ mt: 4 }}>
        <Typography variant="h5" gutterBottom>
          Quick Actions
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <Button
              variant="outlined"
              fullWidth
              startIcon={<SendIcon />}
              sx={{ py: 2 }}
              onClick={() => navigate('/transfer/1')}
            >
              Send Money
            </Button>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Button
              variant="outlined"
              fullWidth
              startIcon={<ReceiptIcon />}
              sx={{ py: 2 }}
              onClick={() => navigate('/beneficiaries')}
            >
              Beneficiaries
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
};

export default Dashboard; 