import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { authAPI } from '../services/api';
import {
  Box,
  Typography,
  Grid,
  TextField,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Alert,
  Container
} from '@mui/material';
import {
  Person as PersonIcon,
  CalendarToday as CalendarIcon,
  CreditCard as CreditCardIcon,
  Work as WorkIcon,
  AttachMoney as MoneyIcon,
} from '@mui/icons-material';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';

const CustomerDetails = () => {
  const [formData, setFormData] = useState({
    dateOfBirth: null,
    aadharNumber: '',
    panNumber: '',
    occupation: '',
    annualIncome: '',
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const { user } = useSelector((state) => state.auth);

  useEffect(() => {
    const fetchCustomerDetails = async () => {
      try {
        const response = await authAPI.getCustomerDetails();
        if (response.data) {
          setFormData({
            dateOfBirth: response.data.dateOfBirth ? new Date(response.data.dateOfBirth) : null,
            aadharNumber: response.data.aadharNumber || '',
            panNumber: response.data.panNumber || '',
            occupation: response.data.occupation || '',
            annualIncome: response.data.annualIncome || '',
          });
        }
      } catch (error) {
        setError('Failed to fetch customer details');
      } finally {
        setLoading(false);
      }
    };

    fetchCustomerDetails();
  }, []);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleDateChange = (date) => {
    setFormData({
      ...formData,
      dateOfBirth: date,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(false);

    try {
      const formattedData = {
        ...formData,
        dateOfBirth: formData.dateOfBirth ? formData.dateOfBirth.toISOString().split('T')[0] : '',
      };
      await authAPI.addCustomerDetails(formattedData);
      setSuccess(true);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to update customer details');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Customer Details
        </Typography>
        <Typography variant="subtitle1" color="text.secondary">
          Manage your personal information
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Customer details updated successfully!
        </Alert>
      )}

      {loading ? (
        <Box display="flex" justifyContent="center" my={4}>
          <CircularProgress />
        </Box>
      ) : (
        <Card>
          <CardContent>
            <Box component="form" onSubmit={handleSubmit}>
              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <LocalizationProvider dateAdapter={AdapterDateFns}>
                    <DatePicker
                      label="Date of Birth"
                      value={formData.dateOfBirth}
                      onChange={handleDateChange}
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          fullWidth
                          name="dateOfBirth"
                          InputLabelProps={{ shrink: true }}
                          InputProps={{
                            ...params.InputProps,
                            startAdornment: <CalendarIcon color="action" sx={{ mr: 1 }} />,
                          }}
                        />
                      )}
                    />
                  </LocalizationProvider>
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Aadhar Number"
                    name="aadharNumber"
                    value={formData.aadharNumber}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: <CreditCardIcon color="action" sx={{ mr: 1 }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="PAN Number"
                    name="panNumber"
                    value={formData.panNumber}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: <CreditCardIcon color="action" sx={{ mr: 1 }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Occupation"
                    name="occupation"
                    value={formData.occupation}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: <WorkIcon color="action" sx={{ mr: 1 }} />,
                    }}
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Annual Income"
                    type="number"
                    name="annualIncome"
                    value={formData.annualIncome}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: <MoneyIcon color="action" sx={{ mr: 1 }} />,
                    }}
                  />
                </Grid>
              </Grid>

              <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
                <Button
                  type="submit"
                  variant="contained"
                  disabled={saving}
                  startIcon={saving ? <CircularProgress size={20} /> : null}
                >
                  {saving ? 'Saving...' : 'Save Changes'}
                </Button>
              </Box>
            </Box>
          </CardContent>
        </Card>
      )}
    </Container>
  );
};

export default CustomerDetails; 