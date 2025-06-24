import React, { useState, useEffect } from 'react';
import { 
  Bus, Users, MapPin, Calendar, TrendingUp, AlertCircle, 
  CheckCircle, Clock, XCircle, RefreshCw, BarChart3 
} from 'lucide-react';

const Dashboard = ({ statistics, onRefresh }) => {
  const [isLoading, setIsLoading] = useState(false);

  const handleRefresh = async () => {
    setIsLoading(true);
    await onRefresh();
    setIsLoading(false);
  };

  // Cartes de statistiques principales
  const StatCard = ({ title, value, icon: Icon, color, description, trend }) => (
    <div className="card hover:shadow-lg transition-all duration-300">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className={`text-3xl font-bold ${color} mt-2`}>
            {value !== undefined ? value : '...'}
          </p>
          {description && (
            <p className="text-xs text-gray-500 mt-1">{description}</p>
          )}
          {trend && (
            <div className="flex items-center mt-2">
              <TrendingUp className="h-4 w-4 text-success-500 mr-1" />
              <span className="text-xs text-success-600">{trend}</span>
            </div>
          )}
        </div>
        <div className={`p-3 rounded-full ${color.replace('text-', 'bg-').replace('-600', '-100')}`}>
          <Icon className={`h-8 w-8 ${color}`} />
        </div>
      </div>
    </div>
  );

  // Composant de statut avec badge
  const StatusBadge = ({ status, count, total, label }) => {
    const getStatusConfig = (status) => {
      switch (status) {
        case 'disponible':
        case 'confirmee':
        case 'planifie':
          return { color: 'badge-success', icon: CheckCircle };
        case 'en_route':
        case 'en_cours':
        case 'en_service':
          return { color: 'badge-info', icon: Clock };
        case 'maintenance':
        case 'en_attente':
          return { color: 'badge-warning', icon: AlertCircle };
        case 'hors_service':
        case 'annule':
          return { color: 'badge-error', icon: XCircle };
        default:
          return { color: 'badge-secondary', icon: AlertCircle };
      }
    };

    const config = getStatusConfig(status);
    const Icon = config.icon;
    const percentage = total > 0 ? Math.round((count / total) * 100) : 0;

    return (
      <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
        <div className="flex items-center space-x-3">
          <Icon className="h-4 w-4 text-gray-500" />
          <span className="text-sm font-medium text-gray-700">{label}</span>
        </div>
        <div className="flex items-center space-x-2">
          <span className={`badge ${config.color}`}>
            {count || 0}
          </span>
          <span className="text-xs text-gray-500">
            {percentage}%
          </span>
        </div>
      </div>
    );
  };

  if (!statistics) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold text-gray-900">Tableau de Bord</h2>
          <button
            onClick={handleRefresh}
            disabled={isLoading}
            className="btn-primary"
          >
            <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
            Actualiser
          </button>
        </div>
        
        {/* Skeleton loading */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="card">
              <div className="animate-pulse">
                <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
                <div className="h-8 bg-gray-200 rounded w-1/2 mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-full"></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-fade-in">
      {/* En-tête */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold text-gray-900">Tableau de Bord</h2>
          <p className="text-gray-600 mt-1">
            Vue d'ensemble de l'agence de transport - Yaoundé
          </p>
        </div>
        <button
          onClick={handleRefresh}
          disabled={isLoading}
          className="btn-primary"
        >
          <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
          Actualiser
        </button>
      </div>

      {/* Statistiques principales */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Bus"
          value={statistics.totalBus}
          icon={Bus}
          color="text-primary-600"
          description="Flotte complète"
          trend="+2 ce mois"
        />
        <StatCard
          title="Chauffeurs"
          value={statistics.totalChauffeurs}
          icon={Users}
          color="text-secondary-600"
          description="Équipe complète"
          trend="100% actifs"
        />
        <StatCard
          title="Trajets"
          value={statistics.totalTrajets}
          icon={MapPin}
          color="text-accent-600"
          description="Planifiés et terminés"
          trend="+15 cette semaine"
        />
        <StatCard
          title="Réservations"
          value={statistics.totalReservations}
          icon={Calendar}
          color="text-success-600"
          description="Total des réservations"
          trend="+8% vs semaine dernière"
        />
      </div>

      {/* Détails par catégorie */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Statut des Bus */}
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-semibold text-gray-900 flex items-center">
              <Bus className="h-5 w-5 mr-2 text-primary-600" />
              État de la Flotte
            </h3>
            <span className="text-sm text-gray-500">
              {statistics.totalBus} bus au total
            </span>
          </div>
          <div className="space-y-3">
            <StatusBadge
              status="disponible"
              count={statistics.busDisponibles}
              total={statistics.totalBus}
              label="Disponibles"
            />
            <StatusBadge
              status="en_route"
              count={statistics.busEnRoute}
              total={statistics.totalBus}
              label="En Route"
            />
            <StatusBadge
              status="maintenance"
              count={statistics.busMaintenance}
              total={statistics.totalBus}
              label="En Maintenance"
            />
          </div>
        </div>

        {/* Statut des Chauffeurs */}
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-semibold text-gray-900 flex items-center">
              <Users className="h-5 w-5 mr-2 text-secondary-600" />
              Équipe de Conduite
            </h3>
            <span className="text-sm text-gray-500">
              {statistics.totalChauffeurs} chauffeurs
            </span>
          </div>
          <div className="space-y-3">
            <StatusBadge
              status="disponible"
              count={statistics.chauffeursDisponibles}
              total={statistics.totalChauffeurs}
              label="Disponibles"
            />
            <StatusBadge
              status="en_service"
              count={statistics.chauffeursEnService}
              total={statistics.totalChauffeurs}
              label="En Service"
            />
          </div>
        </div>

        {/* Statut des Trajets */}
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-semibold text-gray-900 flex items-center">
              <MapPin className="h-5 w-5 mr-2 text-accent-600" />
              Planification des Trajets
            </h3>
            <span className="text-sm text-gray-500">
              {statistics.totalTrajets} trajets
            </span>
          </div>
          <div className="space-y-3">
            <StatusBadge
              status="planifie"
              count={statistics.trajetsPlanifies}
              total={statistics.totalTrajets}
              label="Planifiés"
            />
            <StatusBadge
              status="en_cours"
              count={statistics.trajetsEnCours}
              total={statistics.totalTrajets}
              label="En Cours"
            />
          </div>
        </div>

        {/* Statut des Réservations */}
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-semibold text-gray-900 flex items-center">
              <Calendar className="h-5 w-5 mr-2 text-success-600" />
              Réservations Clients
            </h3>
            <span className="text-sm text-gray-500">
              {statistics.totalReservations} réservations
            </span>
          </div>
          <div className="space-y-3">
            <StatusBadge
              status="confirmee"
              count={statistics.reservationsConfirmees}
              total={statistics.totalReservations}
              label="Confirmées"
            />
            <StatusBadge
              status="en_attente"
              count={statistics.reservationsEnAttente}
              total={statistics.totalReservations}
              label="En Attente"
            />
          </div>
        </div>
      </div>

      {/* Métriques avancées */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="card bg-gradient-primary text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-primary-100 text-sm font-medium">Clients VIP</p>
              <p className="text-3xl font-bold mt-2">{statistics.clientsVip}</p>
              <p className="text-primary-200 text-xs mt-1">Clients privilégiés</p>
            </div>
            <BarChart3 className="h-8 w-8 text-primary-200" />
          </div>
        </div>

        <div className="card bg-gradient-secondary text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-secondary-100 text-sm font-medium">Taux d'Occupation</p>
              <p className="text-3xl font-bold mt-2">
                {statistics.totalTrajets > 0 
                  ? Math.round((statistics.trajetsEnCours / statistics.totalTrajets) * 100)
                  : 0}%
              </p>
              <p className="text-secondary-200 text-xs mt-1">Trajets actifs</p>
            </div>
            <TrendingUp className="h-8 w-8 text-secondary-200" />
          </div>
        </div>

        <div className="card bg-gradient-accent text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-accent-100 text-sm font-medium">Efficacité</p>
              <p className="text-3xl font-bold mt-2">
                {statistics.totalBus > 0 
                  ? Math.round((statistics.busDisponibles / statistics.totalBus) * 100)
                  : 0}%
              </p>
              <p className="text-accent-200 text-xs mt-1">Bus opérationnels</p>
            </div>
            <CheckCircle className="h-8 w-8 text-accent-200" />
          </div>
        </div>
      </div>

      {/* Informations système */}
      <div className="card bg-gray-50">
        <div className="text-center py-4">
          <h4 className="text-lg font-semibold text-gray-900 mb-2">
            Système PolicyAPI - Modélisation Mathématique
          </h4>
          <p className="text-gray-600 text-sm">
            6 politiques mathématiques implémentées • Base de données MySQL • API REST Spring Boot
          </p>
          <div className="flex justify-center items-center space-x-6 mt-4 text-xs text-gray-500">
            <span>✅ Planification de Trajets</span>
            <span>✅ Réservation de Billets</span>
            <span>✅ Affectation de Chauffeurs</span>
            <span>✅ Autorisation de Départ</span>
            <span>✅ Transfert entre Agences</span>
            <span>✅ Maintenance des Véhicules</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;