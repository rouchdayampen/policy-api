import React, { useState, useEffect } from 'react';
import { 
  Bus, Users, MapPin, Calendar, Eye, RefreshCw,
  Filter, Search, ChevronDown, ChevronUp
} from 'lucide-react';
import axios from 'axios';

const DataViewer = () => {
  const [activeEntity, setActiveEntity] = useState('bus');
  const [data, setData] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
  const [filterConfig, setFilterConfig] = useState({});

  // Configuration des entités
  const entities = {
    bus: {
      name: 'Bus',
      icon: Bus,
      endpoint: '/bus',
      color: 'text-primary-600',
      bgColor: 'bg-primary-50'
    },
    chauffeurs: {
      name: 'Chauffeurs',
      icon: Users,
      endpoint: '/chauffeurs',
      color: 'text-secondary-600',
      bgColor: 'bg-secondary-50'
    },
    trajets: {
      name: 'Trajets',
      icon: MapPin,
      endpoint: '/trajets',
      color: 'text-accent-600',
      bgColor: 'bg-accent-50'
    },
    utilisateurs: {
      name: 'Utilisateurs',
      icon: Users,
      endpoint: '/utilisateurs',
      color: 'text-success-600',
      bgColor: 'bg-success-50'
    },
    reservations: {
      name: 'Réservations',
      icon: Calendar,
      endpoint: '/reservations',
      color: 'text-warning-600',
      bgColor: 'bg-warning-50'
    }
  };

  // Chargement des données
  useEffect(() => {
    loadData(activeEntity);
  }, [activeEntity]);

  const loadData = async (entityKey) => {
    setIsLoading(true);
    try {
      const entity = entities[entityKey];
      const response = await axios.get(entity.endpoint);
      setData(prev => ({
        ...prev,
        [entityKey]: response.data
      }));
    } catch (error) {
      console.error(`Erreur lors du chargement des ${entityKey}:`, error);
    } finally {
      setIsLoading(false);
    }
  };

  // Fonction de tri
  const handleSort = (key) => {
    let direction = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  // Fonction de filtrage et tri
  const getFilteredAndSortedData = () => {
    let items = data[activeEntity] || [];

    // Filtrage par recherche
    if (searchTerm) {
      items = items.filter(item => {
        return Object.values(item).some(value => 
          value && value.toString().toLowerCase().includes(searchTerm.toLowerCase())
        );
      });
    }

    // Filtrage par statut
    if (filterConfig.status) {
      items = items.filter(item => item.statut === filterConfig.status);
    }

    // Tri
    if (sortConfig.key) {
      items.sort((a, b) => {
        let aValue = a[sortConfig.key];
        let bValue = b[sortConfig.key];

        if (aValue < bValue) {
          return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (aValue > bValue) {
          return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
      });
    }

    return items;
  };

  // Badge de statut
  const StatusBadge = ({ status }) => {
    const getStatusConfig = (status) => {
      switch (status?.toLowerCase()) {
        case 'disponible':
        case 'confirmee':
        case 'planifie':
          return { color: 'badge-success', text: status };
        case 'en_route':
        case 'en_cours':
        case 'en_service':
          return { color: 'badge-info', text: status.replace('_', ' ') };
        case 'maintenance':
        case 'en_attente':
          return { color: 'badge-warning', text: status.replace('_', ' ') };
        case 'hors_service':
        case 'annule':
          return { color: 'badge-error', text: status.replace('_', ' ') };
        default:
          return { color: 'badge-secondary', text: status || 'N/A' };
      }
    };

    const config = getStatusConfig(status);
    return <span className={`badge ${config.color}`}>{config.text}</span>;
  };

  // Rendu des tableaux spécifiques
  const renderTable = () => {
    const items = getFilteredAndSortedData();
    
    if (items.length === 0) {
      return (
        <div className="text-center py-12">
          <Eye className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500">Aucune donnée disponible</p>
        </div>
      );
    }

    switch (activeEntity) {
      case 'bus':
        return (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                      onClick={() => handleSort('immatriculation')}>
                    <div className="flex items-center space-x-1">
                      <span>Immatriculation</span>
                      {sortConfig.key === 'immatriculation' && (
                        sortConfig.direction === 'asc' ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />
                      )}
                    </div>
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Capacité</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Agence</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Passagers</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {items.map((bus) => (
                  <tr key={bus.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {bus.immatriculation}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      <span className={`badge ${bus.type === 'VIP' ? 'badge-info' : 'badge-secondary'}`}>
                        {bus.type}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {bus.capacite} places
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <StatusBadge status={bus.statut} />
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {bus.agenceActuelle}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {bus.passagersActuels}/{bus.capacite}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        );

      case 'chauffeurs':
        return (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nom</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Permis</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Agence</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Heures</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {items.map((chauffeur) => (
                  <tr key={chauffeur.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {chauffeur.prenom} {chauffeur.nom}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {chauffeur.numeroPermis}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <StatusBadge status={chauffeur.statut} />
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {chauffeur.agenceActuelle}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {chauffeur.heuresTravaillees}/8h
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        );

      case 'trajets':
        return (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trajet</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Départ</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Arrivée</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Prix</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Réservations</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {items.map((trajet) => (
                  <tr key={trajet.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {trajet.agenceOrigine} → {trajet.agenceDestination}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(trajet.dateDepart).toLocaleString('fr-FR')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(trajet.dateArrivee).toLocaleString('fr-FR')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {trajet.prix} FCFA
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <StatusBadge status={trajet.statut} />
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {trajet.placesReservees} places
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        );

      case 'utilisateurs':
        return (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nom</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Solde</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Voyages</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {items.map((utilisateur) => (
                  <tr key={utilisateur.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {utilisateur.prenom} {utilisateur.nom}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {utilisateur.email}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`badge ${
                        utilisateur.typeClient === 'VIP' ? 'badge-info' :
                        utilisateur.typeClient === 'REGULIER' ? 'badge-success' : 'badge-secondary'
                      }`}>
                        {utilisateur.typeClient}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {utilisateur.soldeCompte} FCFA
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {utilisateur.nombreVoyages}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        );

      case 'reservations':
        return (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">N° Réservation</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Client</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trajet</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Places</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Montant</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {items.map((reservation) => (
                  <tr key={reservation.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {reservation.numeroReservation}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {reservation.utilisateur?.prenom} {reservation.utilisateur?.nom}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {reservation.trajet?.agenceOrigine} → {reservation.trajet?.agenceDestination}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {reservation.nombrePlaces}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {reservation.montantTotal} FCFA
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <StatusBadge status={reservation.statut} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        );

      default:
        return null;
    }
  };

  const currentEntity = entities[activeEntity];
  const Icon = currentEntity.icon;
  const items = getFilteredAndSortedData();

  return (
    <div className="space-y-6 animate-fade-in">
      {/* En-tête */}
      <div>
        <h2 className="text-3xl font-bold text-gray-900 mb-2">
          Visualisation des Données
        </h2>
        <p className="text-gray-600">
          Explorez les entités du système et leurs relations
        </p>
      </div>

      {/* Navigation des entités */}
      <div className="flex flex-wrap gap-2">
        {Object.entries(entities).map(([key, entity]) => {
          const EntityIcon = entity.icon;
          return (
            <button
              key={key}
              onClick={() => setActiveEntity(key)}
              className={`flex items-center space-x-2 px-4 py-2 rounded-lg border transition-all duration-200 ${
                activeEntity === key
                  ? `border-primary-500 ${entity.bgColor} ${entity.color}`
                  : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50 text-gray-600'
              }`}
            >
              <EntityIcon className="h-4 w-4" />
              <span className="font-medium">{entity.name}</span>
              <span className="text-xs bg-white px-2 py-0.5 rounded-full">
                {data[key]?.length || 0}
              </span>
            </button>
          );
        })}
      </div>

      {/* Contrôles */}
      <div className="card">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-4 sm:space-y-0">
          <div className="flex items-center space-x-4">
            <div className={`p-2 rounded-lg ${currentEntity.bgColor}`}>
              <Icon className={`h-5 w-5 ${currentEntity.color}`} />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900">
                {currentEntity.name}
              </h3>
              <p className="text-sm text-gray-500">
                {items.length} élément{items.length > 1 ? 's' : ''} affiché{items.length > 1 ? 's' : ''}
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-4">
            {/* Recherche */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <input
                type="text"
                placeholder="Rechercher..."
                className="form-input pl-10 w-64"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            {/* Actualiser */}
            <button
              onClick={() => loadData(activeEntity)}
              disabled={isLoading}
              className="btn-secondary"
            >
              <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
              Actualiser
            </button>
          </div>
        </div>
      </div>

      {/* Tableau */}
      <div className="card p-0">
        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <div className="loading-spinner mr-3" />
            <span className="text-gray-500">Chargement des données...</span>
          </div>
        ) : (
          renderTable()
        )}
      </div>
    </div>
  );
};

export default DataViewer;