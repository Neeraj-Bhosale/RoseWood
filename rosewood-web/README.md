# RoseWood Web App

Full-stack web version of the RoseWood Restaurant Management System.

## 📁 Structure

```
rosewood-web/
├── backend/     Spring Boot 3 REST API (Java 17)
└── frontend/    React 18 + Vite
```

## 🚀 Run Locally

### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

## 🌐 Deploy to Production

### Backend → Railway
1. Go to [railway.app](https://railway.app)
2. New Project → Deploy from GitHub → select `rosewood-web/backend`
3. Add environment variables:
   - `SPRING_DATASOURCE_URL` = your Aiven MySQL URL
   - `SPRING_DATASOURCE_USERNAME` = your DB username
   - `SPRING_DATASOURCE_PASSWORD` = your DB password
   - `JWT_SECRET` = any long random string
   - `CORS_ORIGIN` = your Vercel frontend URL

### Frontend → Vercel
1. Go to [vercel.com](https://vercel.com)
2. New Project → Import GitHub repo → set **Root Directory** to `rosewood-web/frontend`
3. Add environment variable:
   - `VITE_API_URL` = your Railway backend URL

### Database → Aiven MySQL (Free)
1. Go to [aiven.io](https://aiven.io) → Create MySQL service (free tier)
2. Copy connection URL and credentials
3. Run `database/schema.sql` then `database/sample_data.sql`

## 🔑 Default Login
After importing sample data from the original JavaFX app, use the same credentials you created there.
Or register a new account via `/register`.

## 📡 API Endpoints

| Endpoint | Description |
|----------|-------------|
| POST /api/auth/login | Login |
| POST /api/auth/register | Register |
| GET  /api/orders | All orders |
| POST /api/orders | Create order (deducts inventory) |
| GET  /api/inventory | All inventory |
| GET  /api/inventory/low-stock | Low stock alerts |
| GET  /api/menu/items | All menu items |
| GET  /api/suppliers | All suppliers |
| GET  /api/customers | All customers |
| GET  /api/tables | All tables |
| GET  /api/orders/dashboard/stats | Dashboard stats |
