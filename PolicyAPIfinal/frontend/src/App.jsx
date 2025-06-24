import React, { useState, useEffect } from 'react';
import { Bus, Users, MapPin, BarChart3, Settings, AlertTriangle } from 'lucide-react';
import Dashboard from './components/Dashboard';
import PolicyForm from './components/PolicyForm';
import DataViewer from './components/DataViewer';
import axios from 'axios';

// Configuration de l'API
const API_BASE_URL = 'http://localhost:8080/api';

// Configuration axios
axios.defaults.baseURL = API_BASE_URL;
axios.defaults.headers.common['Content-Type'] = 'application/json';

function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [apiStatus, setApiStatus] = useState('checking');
  const [statistics, setStatistics] = useState(null);

  // Vérification de la santé de l'API au démarrage
  useEffect(() => {
    checkApiHealth();
    if (activeTab === 'dashboard') {
      loadStatistics();
    }
  }, [activeTab]);

  const checkApiHealth = async () => {
    try {
      const response = await axios.get('/health');
      if (response.data.status === 'UP') {
        setApiStatus('connected');
      } else {
        setApiStatus('error');
      }
    } catch (error) {
      console.error('Erreur de connexion API:', error);
      setApiStatus('error');
    }
  };

  const loadStatistics = async ()=> {
    try {
      const response = await axios.get('/statistics');
      setStatistics(response.data);
    } catch (error) {
      console.error('Erreur lors du chargement des statistiques:', error);
    }
  };

  const tabs = [
    {
      id: 'dashboard',
      name: 'Tableau de Bord',
      icon: BarChart3,
      description: 'Vue d\'ensemble et statistiques'
    },
    {
      id: 'policies',
      name: 'Politiques',
      icon: Settings,
      description: 'Test des politiques (Policies)'
    },
    {
      id: 'data',
      name: 'Données',
      icon: Bus,
      description: 'Visualisation des entités'
    }
  ];

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return <Dashboard statistics={statistics} onRefresh={loadStatistics} />;
      case 'policies':
        return <PolicyForm />;
      case 'data':
        return <DataViewer />;
      default:
        return <Dashboard statistics={statistics} onRefresh={loadStatistics} />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            {/* Logo et titre */}
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <div className="p-2 bg-primary-600 rounded-lg">
                  <MapPin className="h-6 w-6 text-white" />
                </div>
                <div>
                  <h1 className="text-xl font-bold text-gray-900">PolicyAPI</h1>
                  <p className="text-xs text-gray-500">Externalisation des Policies (Politiques)</p>
                </div>
              </div>
            </div>

            {/* Statut de l'API */}
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <div className={`h-2 w-2 rounded-full ${
                  apiStatus === 'connected' ? 'bg-success-500' : 
                  apiStatus === 'error' ? 'bg-error-500' : 'bg-warning-500'
                }`} />
                <span className="text-sm text-gray-600">
                  {apiStatus === 'connected' ? 'API Connectée' : 
                   apiStatus === 'error' ? 'API Déconnectée' : 'Vérification...'}
                </span>
              </div>
              
              {apiStatus === 'error' && (
                <button
                  onClick={checkApiHealth}
                  className="btn-secondary text-xs"
                >
                  Reconnecter
                </button>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Message d'alerte si API déconnectée */}
      {apiStatus === 'error' && (
        <div className="bg-error-50 border-l-4 border-error-400 p-4">
          <div className="flex items-center max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <AlertTriangle className="h-5 w-5 text-error-400 mr-3" />
            <div>
              <p className="text-error-800 font-medium">
                Impossible de se connecter au backend
              </p>
              <p className="text-error-700 text-sm">
                Assurez-vous que le serveur Spring Boot est démarré sur http://localhost:8080
              </p>
            </div>
          </div>
        </div>
      )}

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Navigation par onglets */}
        <div className="mb-8">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8">
              {tabs.map((tab) => {
                const Icon = tab.icon;
                return (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`group inline-flex items-center py-4 px-1 border-b-2 font-medium text-sm transition-colors duration-200 ${
                      activeTab === tab.id
                        ? 'border-primary-500 text-primary-600'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                    }`}
                  >
                    <Icon className={`mr-2 h-5 w-5 ${
                      activeTab === tab.id ? 'text-primary-500' : 'text-gray-400 group-hover:text-gray-500'
                    }`} />
                    <div className="text-left">
                      <div>{tab.name}</div>
                      <div className="text-xs text-gray-400 font-normal">{tab.description}</div>
                    </div>
                  </button>
                );
              })}
            </nav>
          </div>
        </div>

        {/* Contenu principal */}
        <main className="animate-fade-in">
          {renderContent()}
        </main>
      </div>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 mt-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center">
            <p className="text-gray-600 text-sm">
              PolicyAPI - API d'externalisation et planifications des Policies(Politiques)
            </p>
            <p className="text-gray-500 text-xs mt-1">
              Agence de Transport • Cameroun
            </p>
            <div className="mt-4 text-xs text-gray-400">
              <p>Développé par: HEUDEP DJANDJA Brian B • NJEMPOU YAMPEN Rachida R • NZUNGANG MBOUM Freddy L</p>
              <p>Classe: 3GI ENSPY • Université de Yaoundé I</p>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;