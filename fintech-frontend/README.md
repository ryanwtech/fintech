# Fintech Frontend

A modern React TypeScript frontend for the Fintech Backend API, built with Vite, Tailwind CSS, and React Query.

## 🚀 Features

- **Modern React 19** with TypeScript
- **Vite** for fast development and building
- **Tailwind CSS** for styling
- **React Query** for server state management
- **React Router** for navigation
- **React Hook Form** with Zod validation
- **Axios** for API communication
- **Recharts** for data visualization
- **Lucide React** for icons

## 🛠️ Tech Stack

- **React 19** with TypeScript
- **Vite** (Build tool)
- **Tailwind CSS** (Styling)
- **React Query** (Server state management)
- **React Router DOM** (Routing)
- **React Hook Form** (Form handling)
- **Zod** (Schema validation)
- **Axios** (HTTP client)
- **Recharts** (Charts)
- **Lucide React** (Icons)

## 📋 Prerequisites

- Node.js 18 or higher
- npm or yarn
- Running Fintech Backend API

## 🚀 Quick Start

1. **Install dependencies**
   ```bash
   npm install
   ```

2. **Set up environment variables**
   ```bash
   cp env.example .env
   # Edit .env with your API URL
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

4. **Open in browser**
   ```
   http://localhost:5173
   ```

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the root directory:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### API Configuration

The frontend is configured to work with the Fintech Backend API. Make sure the backend is running on the configured URL.

## 📁 Project Structure

```
src/
├── components/          # Reusable components
│   ├── Layout.tsx      # Main layout component
│   └── ProtectedRoute.tsx # Route protection
├── hooks/              # Custom React hooks
│   ├── useAuth.ts      # Authentication hooks
│   ├── useAccounts.ts  # Account management hooks
│   └── useTransactions.ts # Transaction hooks
├── lib/                # Utility libraries
│   ├── api.ts          # Axios configuration
│   ├── schemas.ts      # Zod validation schemas
│   └── utils.ts        # Utility functions
├── pages/              # Page components
│   ├── LoginPage.tsx   # Login page
│   ├── RegisterPage.tsx # Registration page
│   ├── DashboardPage.tsx # Dashboard
│   ├── AccountsPage.tsx # Accounts management
│   ├── AccountDetailPage.tsx # Account details
│   ├── TransactionsPage.tsx # Transactions
│   ├── BudgetsPage.tsx # Budget management
│   ├── ReportsPage.tsx # Reports and analytics
│   ├── RulesPage.tsx   # Categorization rules
│   └── SettingsPage.tsx # Settings
├── types/              # TypeScript type definitions
│   └── index.ts        # Main types
├── App.tsx             # Main app component
├── main.tsx            # App entry point
└── index.css           # Global styles
```

## 🎨 Styling

The project uses Tailwind CSS for styling with a custom design system:

- **Colors**: Custom color palette with dark mode support
- **Components**: Reusable component styles
- **Responsive**: Mobile-first responsive design
- **Accessibility**: WCAG compliant components

## 🔐 Authentication

The app uses JWT-based authentication:

1. **Login**: Users can log in with username/password
2. **Registration**: New users can create accounts
3. **Protected Routes**: Routes are protected by authentication
4. **Token Management**: Automatic token handling and refresh

## 📊 State Management

- **React Query**: Server state management
- **React Hook Form**: Form state management
- **Local Storage**: Token persistence
- **Context**: Global app state (if needed)

## 🧪 Development

### Available Scripts

```bash
# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

### Development Workflow

1. **Start backend**: Ensure the Fintech Backend API is running
2. **Start frontend**: Run `npm run dev`
3. **Open browser**: Navigate to `http://localhost:5173`
4. **Login**: Use test credentials (testuser/password123)

## 🚀 Deployment

### Build for Production

```bash
npm run build
```

The built files will be in the `dist` directory.

### Environment Variables for Production

Set the following environment variables:

```env
VITE_API_BASE_URL=https://your-api-domain.com/api
```

### Docker Deployment

```dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 🔗 API Integration

The frontend integrates with the following API endpoints:

- **Authentication**: `/auth/login`, `/auth/register`, `/auth/profile`
- **Accounts**: `/accounts`
- **Transactions**: `/accounts/{id}/transactions`, `/transactions`
- **Categories**: `/categories`
- **Budgets**: `/budgets`
- **Reports**: `/reports/cashflow`, `/reports/spend-by-category`, `/reports/trend`
- **Rules**: `/rules`

## 🎯 Features

### Implemented
- ✅ User authentication (login/register)
- ✅ Protected routing
- ✅ Account management
- ✅ Dashboard with overview
- ✅ Responsive design
- ✅ Form validation
- ✅ API integration

### Planned
- 🔄 Transaction management
- 🔄 Budget creation and tracking
- 🔄 Reports and analytics
- 🔄 Rule management
- 🔄 CSV import
- 🔄 Real-time updates
- 🔄 Dark mode
- 🔄 PWA support

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Check the API documentation
- Review the backend logs
- Create an issue in the repository

---

**Happy coding! 🎉**