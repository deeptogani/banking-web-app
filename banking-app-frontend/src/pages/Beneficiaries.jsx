import React, { useState, useEffect } from 'react';
import { authAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  Box
} from '@mui/material';

const Beneficiaries = () => {
  const [beneficiaries, setBeneficiaries] = useState([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const [newBeneficiary, setNewBeneficiary] = useState({
    name: '',
    bankName: '',
    accountNumber: '',
    ifscCode: '',
    maxTransferLimit: '',
    relationship: ''
  });

  useEffect(() => {
    fetchBeneficiaries();
  }, []);

  const fetchBeneficiaries = async () => {
    try {
      const response = await authAPI.getBeneficiaries();
      if (response.data.success) {
        setBeneficiaries(response.data.beneficiaries);
      }
    } catch (error) {
      setError('Failed to fetch beneficiaries');
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewBeneficiary(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleAddBeneficiary = async () => {
    try {
      const response = await authAPI.addBeneficiary(newBeneficiary);
      if (response.data.success) {
        setSuccess('Beneficiary added successfully');
        setOpenDialog(false);
        setNewBeneficiary({
          name: '',
          bankName: '',
          accountNumber: '',
          ifscCode: '',
          maxTransferLimit: '',
          relationship: ''
        });
        fetchBeneficiaries();
      }
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to add beneficiary');
    }
  };

  const handleTransfer = (beneficiaryId) => {
    navigate(`/transfer/${beneficiaryId}`);
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Beneficiaries
        </Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={() => setOpenDialog(true)}
        >
          Add New Beneficiary
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Bank Name</TableCell>
              <TableCell>Account Number</TableCell>
              <TableCell>IFSC Code</TableCell>
              <TableCell>Max Transfer Limit</TableCell>
              <TableCell>Relationship</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {beneficiaries.map((beneficiary) => (
              <TableRow key={beneficiary.beneficiaryId}>
                <TableCell>{beneficiary.name}</TableCell>
                <TableCell>{beneficiary.bankName}</TableCell>
                <TableCell>{beneficiary.accountNumber}</TableCell>
                <TableCell>{beneficiary.ifscCode}</TableCell>
                <TableCell>{beneficiary.maxTransferLimit}</TableCell>
                <TableCell>{beneficiary.relationship}</TableCell>
                <TableCell>
                  <Button
                    variant="contained"
                    color="primary"
                    size="small"
                    onClick={() => handleTransfer(beneficiary.beneficiaryId)}
                  >
                    Transfer
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>Add New Beneficiary</DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            name="name"
            label="Name"
            type="text"
            fullWidth
            value={newBeneficiary.name}
            onChange={handleInputChange}
            required
          />
          <TextField
            margin="dense"
            name="bankName"
            label="Bank Name"
            type="text"
            fullWidth
            value={newBeneficiary.bankName}
            onChange={handleInputChange}
            required
          />
          <TextField
            margin="dense"
            name="accountNumber"
            label="Account Number"
            type="text"
            fullWidth
            value={newBeneficiary.accountNumber}
            onChange={handleInputChange}
            required
          />
          <TextField
            margin="dense"
            name="ifscCode"
            label="IFSC Code"
            type="text"
            fullWidth
            value={newBeneficiary.ifscCode}
            onChange={handleInputChange}
          />
          <TextField
            margin="dense"
            name="maxTransferLimit"
            label="Max Transfer Limit"
            type="number"
            fullWidth
            value={newBeneficiary.maxTransferLimit}
            onChange={handleInputChange}
          />
          <TextField
            margin="dense"
            name="relationship"
            label="Relationship"
            type="text"
            fullWidth
            value={newBeneficiary.relationship}
            onChange={handleInputChange}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleAddBeneficiary} variant="contained" color="primary">
            Add
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Beneficiaries; 