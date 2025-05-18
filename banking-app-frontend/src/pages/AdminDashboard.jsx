import React, { useEffect, useState } from 'react';
import { 
  Box, 
  Container, 
  Typography, 
  Paper, 
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Button,
  Card,
  CardContent,
  Stack,
  CircularProgress,
  Alert
} from '@mui/material';
import { useAuth } from '../store/AuthContext';
import AdminNavbar from '../components/AdminNavbar';
import { useNavigate } from 'react-router-dom';
import { API_URL } from '../services/api';

const AdminDashboard = () => {
  const { token } = useAuth();
  const [adminInfo, setAdminInfo] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchAdminInfo = async () => {
      try {
        const response = await fetch(API_URL + '/auth/me', {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
        const data = await response.json();
        setAdminInfo(data);
      } catch (error) {
        console.error('Error fetching admin info:', error);
      }
    };

    fetchAdminInfo();
  }, [token]);

  useEffect(() => {
    const fetchTransactions = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(
          `${API_URL}/admin/transactions?page=${page}&size=${rowsPerPage}`,
          {
            headers: {
              'Authorization': `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error('Failed to fetch transactions');
        }

        const data = await response.json();
        setTransactions(data.transactions || []);
        setTotalItems(data.totalItems || 0);
        setTotalPages(data.totalPages || 0);
      } catch (error) {
        console.error('Error fetching transactions:', error);
        setError('Failed to load transactions. Please try again later.');
        setTransactions([]);
        setTotalItems(0);
        setTotalPages(0);
      } finally {
        setLoading(false);
      }
    };

    if (token) {
      fetchTransactions();
    }
  }, [token, page, rowsPerPage]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <AdminNavbar />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4, flex: 1 }}>
        <Grid container spacing={3}>
          {/* Admin Info Card */}
          <Grid item xs={12} md={4}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Admin Information
                </Typography>
                {adminInfo && (
                  <Stack spacing={1}>
                    <Typography>
                      Name: {adminInfo.firstName} {adminInfo.lastName}
                    </Typography>
                    <Typography>
                      Email: {adminInfo.email}
                    </Typography>
                    <Typography>
                      Username: {adminInfo.username}
                    </Typography>
                  </Stack>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Quick Actions Card */}
          <Grid item xs={12} md={8}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Quick Actions
                </Typography>
                <Stack direction="row" spacing={2}>
                  <Button 
                    variant="contained" 
                    color="primary"
                    onClick={() => navigate('/admin/users')}
                  >
                    View All Users
                  </Button>
                </Stack>
              </CardContent>
            </Card>
          </Grid>

          {/* Transactions Table */}
          <Grid item xs={12}>
            <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column' }}>
              <Typography component="h2" variant="h6" color="primary" gutterBottom>
                Recent Transactions
              </Typography>
              {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {error}
                </Alert>
              )}
              {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                  <CircularProgress />
                </Box>
              ) : (
                <>
                  <TableContainer>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>Transaction ID</TableCell>
                          <TableCell>From Account</TableCell>
                          <TableCell>To Account</TableCell>
                          <TableCell>Bank Name</TableCell>
                          <TableCell>Amount</TableCell>
                          <TableCell>Type</TableCell>
                          <TableCell>Status</TableCell>
                          <TableCell>Date</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {transactions.length > 0 ? (
                          transactions.map((transaction) => (
                            <TableRow key={transaction.transactionId}>
                              <TableCell>{transaction.transactionId}</TableCell>
                              <TableCell>{transaction.fromAccountNumber}</TableCell>
                              <TableCell>{transaction.toAccountNumber}</TableCell>
                              <TableCell>{transaction.toBankName || 'Internal Transfer'}</TableCell>
                              <TableCell>₹{transaction.amount}</TableCell>
                              <TableCell>{transaction.transactionType}</TableCell>
                              <TableCell>{transaction.status}</TableCell>
                              <TableCell>
                                {new Date(transaction.transactionDate).toLocaleString()}
                              </TableCell>
                            </TableRow>
                          ))
                        ) : (
                          <TableRow>
                            <TableCell colSpan={8} align="center">
                              No transactions found
                            </TableCell>
                          </TableRow>
                        )}
                      </TableBody>
                    </Table>
                  </TableContainer>
                  <TablePagination
                    component="div"
                    count={totalItems}
                    page={page}
                    onPageChange={handleChangePage}
                    rowsPerPage={rowsPerPage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                    rowsPerPageOptions={[5, 10, 25]}
                  />
                </>
              )}
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};

export default AdminDashboard; 