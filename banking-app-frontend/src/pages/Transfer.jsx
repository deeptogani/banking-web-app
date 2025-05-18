import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import {
  Container,
  Typography,
  TextField,
  Button,
  Paper,
  Alert,
  Box,
  CircularProgress
} from '@mui/material';

const Transfer = () => {
  const { beneficiaryId } = useParams();
  const navigate = useNavigate();
  const [beneficiary, setBeneficiary] = useState(null);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchBeneficiaryDetails();
  }, [beneficiaryId]);

  const fetchBeneficiaryDetails = async () => {
    try {
      const response = await authAPI.getBeneficiaries();
      if (response.data.success) {
        const foundBeneficiary = response.data.beneficiaries.find(
          b => b.beneficiaryId === parseInt(beneficiaryId)
        );
        if (foundBeneficiary) {
          setBeneficiary(foundBeneficiary);
        } else {
          setError('No beneficiaries available. Please add a beneficiary first.');
        }
      }
    } catch (error) {
      setError('Failed to fetch beneficiary details');
    } finally {
      setLoading(false);
    }
  };

  const handleTransfer = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await authAPI.transferToBeneficiary({
        beneficiaryId: parseInt(beneficiaryId),
        amount: parseFloat(amount),
        description
      });

      if (response.data.success) {
        setSuccess('Transfer initiated successfully');
        setAmount('');
        setDescription('');
        // Navigate to transaction history after successful transfer
        setTimeout(() => {
          navigate('/dashboard');
        }, 2000);
      }
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to initiate transfer');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container maxWidth="sm" sx={{ mt: 4, textAlign: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="sm" sx={{ mt: 4 }}>
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>
          <Button
            variant="contained"
            color="primary"
            onClick={() => navigate('/beneficiaries')}
          >
            Add Beneficiary
          </Button>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h5" component="h1" gutterBottom>
          Transfer to {beneficiary.name}
        </Typography>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

        <Box component="form" onSubmit={handleTransfer}>
          <TextField
            margin="normal"
            label="Beneficiary Name"
            value={beneficiary.name}
            fullWidth
            disabled
          />
          <TextField
            margin="normal"
            label="Bank Name"
            value={beneficiary.bankName}
            fullWidth
            disabled
          />
          <TextField
            margin="normal"
            label="Account Number"
            value={beneficiary.accountNumber}
            fullWidth
            disabled
          />
          <TextField
            margin="normal"
            label="Amount"
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            fullWidth
            required
            inputProps={{ min: "0.01", step: "0.01" }}
          />
          <TextField
            margin="normal"
            label="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
            multiline
            rows={2}
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            sx={{ mt: 3 }}
            disabled={loading}
          >
            {loading ? 'Processing...' : 'Transfer'}
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default Transfer; 