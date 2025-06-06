# Banking App Frontend

This is the frontend application for the Banking App, built with React and Material-UI.

## Features

- User authentication (login/register)
- Account overview
- Transaction history
- Money transfer
- Bill payments
- Responsive design

## Prerequisites

- Node.js (v14 or higher)
- npm or yarn

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   # or
   yarn install
   ```

2. Create a `.env` file in the root directory and add the following:
   ```
   VITE_API_URL=http://localhost:8080
   ```

3. Start the development server:
   ```bash
   npm run dev
   # or
   yarn dev
   ```

4. Open [http://localhost:5173](http://localhost:5173) in your browser.

## Available Scripts

- `npm run dev` - Start the development server
- `npm run build` - Build the app for production
- `npm run preview` - Preview the production build locally
- `npm run lint` - Run ESLint to check for code issues

## Project Structure

```
src/
  ├── components/     # Reusable UI components
  ├── contexts/       # React contexts
  ├── pages/         # Page components
  ├── services/      # API services
  ├── store/         # Redux store and slices
  ├── theme.js       # Material-UI theme configuration
  ├── App.jsx        # Main application component
  └── main.jsx       # Application entry point
```

## Technologies Used

- React
- Material-UI
- Redux Toolkit
- Redux Persist
- React Router
- Axios
- Vite

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.
#   b a n k i n g - a p p - f r o n t e n d  
 