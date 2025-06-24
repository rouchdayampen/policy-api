import React, { useState, useEffect } from 'react';
import { 
  Settings, Play, CheckCircle, XCircle, Clock, 
  Bus, Users, MapPin, Calendar, Wrench, ArrowRightLeft 
} from 'lucide-react';
import axios from 'axios';

const PolicyForm = () => {
  const [selectedPolicy, setSelectedPolicy] = useState('planification');
  const [formData, setFormData] = useState({});
  const [result, setResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [entities, setEntities] = useState({
    bus: [],
    chauffeurs: [],
    trajets: [],
    utilisateurs: []
  });

  // Chargement des entités au démarrage
  useEffect(() => {
    loadEntities();
  }, []);

  const loadEntities = async () => {
    try {
      const [busRes, chauffeursRes, trajetsRes, utilisateursRes] = await Promise.all([
        axios.get('/bus'),
        axios.get('/chauffeurs'),
        axios.get('/trajets'),
        axios.get('/utilisateurs')
      ]);

      setEntities({
        bus: busRes.data,
        chauffeurs: chauffeursRes.data,
        trajets: trajetsRes.data,
        utilisateurs: utilisateursRes.data
      });
    } catch (error) {
      console.error('Erreur lors du chargement des entités:', error);
    }
  };

  // Configuration des politiques
  const policies = {
    planification: {
      name: 'Planification de Trajet',
      icon: MapPin,
      description: 'Policy(PLANIFICATION_TRAJET, (t, h)) = ALLOW ⟺ TypeBus(b) ∧ EspaceDisponible(ad,h) ∧ CapaciteBus(b,d) ∧ BusDisponible(a0,b,t,h) ∧ ChauffeurDisponible(a0,c)',
      endpoint: '/policies/planification',
      color: 'text-primary-600'
    },
    reservation: {
      name: 'Réservation de Billet',
      icon: Calendar,
      description: 'Policy(RESERVATION, r) = ALLOW ⟺ TrajetExiste(t) ∧ DateTimeReservationValide(t) ∧ PlaceDisponible(b,t) ∧ PaiementEffectue(u)',
      endpoint: '/policies/reservation',
      color: 'text-success-600'
    },
    affectation: {
      name: 'Affectation de Chauffeur',
      icon: Users,
      description: 'Policy(AFFECTATION_CHAUFFEUR,(c,d)) = ALLOW ⟺ ChauffeurDisponible(c,a0,d)',
      endpoint: '/policies/affectation-chauffeur',
      color: 'text-secondary-600'
    },
    depart: {
      name: 'Départ de Bus',
      icon: Bus,
      description: 'Politique différenciée VIP/Classique selon le type de bus et le taux de remplissage',
      endpoint: '/policies/depart-bus',
      color: 'text-accent-600'
    },
    transfert: {
      name: 'Transfert entre Agences',
      icon: ArrowRightLeft,
      description: 'Policy(TRANSFERT_AGENCE, (b, adest, d)) = ALLOW ⟺ BusExiste(asrc, *, b, d) ∧ ChauffeurDisponible(asrc, c) ∧ (asrc ≠ adest) ∧ EspaceDisponible(adest, d)',
      endpoint: '/policies/transfert-agence',
      color: 'text-warning-600'
    },
    maintenance: {
      name: 'Maintenance de Véhicule',
      icon: Wrench,
      description: 'Policy(MAINTENANCE, b) = ALLOW ⟺ EtatCritique(b)',
      endpoint: '/policies/maintenance',
      color: 'text-error-600'
    }
  };

  const agences = [
    'Yaoundé Centre',
    'Douala Port',
    'Bafoussam',
    'Bamenda',
    'Garoua'
  ];

  // Gestion des changements de formulaire
  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  // Soumission du formulaire
  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setResult(null);

    try {
      const policy = policies[selectedPolicy];
      let response;

      // Appel API selon la politique sélectionnée
      if (selectedPolicy === 'depart' || selectedPolicy === 'maintenance') {
        // Ces politiques prennent seulement un ID en paramètre d'URL
        const id = selectedPolicy === 'depart' ? formData.trajetId : formData.busId;
        response = await axios.post(`${policy.endpoint}/${id}`);
      } else {
        // Les autres politiques prennent un body JSON
        response = await axios.post(policy.endpoint, formData);
      }

      setResult(response.data);
      
      // Recharger les entités après une opération réussie
      if (response.data.decision === 'ALLOW') {
        await loadEntities();
      }
    } catch (error) {
      console.error('Erreur lors de l\'évaluation de la politique:', error);
      setResult({
        decision: 'ERROR',
        explication: `Erreur: ${error.response?.data?.error || error.message}`,
        timestamp: new Date().toISOString()
      });
    } finally {
      setIsLoading(false);
    }
  };

  // Rendu des formulaires spécifiques à chaque politique
  const renderPolicyForm = () => {
    switch (selectedPolicy) {
      case 'planification':
        return (
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="form-group">
                <label className="form-label">Agence d'Origine</label>
                <select
                  className="form-select"
                  value={formData.agenceOrigine || ''}
                  onChange={(e) => handleInputChange('agenceOrigine', e.target.value)}
                  required
                >
                  <option value="">Sélectionner une agence</option>
                  {agences.map(agence => (
                    <option key={agence} value={agence}>{agence}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Agence de Destination</label>
                <select
                  className="form-select"
                  value={formData.agenceDestination || ''}
                  onChange={(e) => handleInputChange('agenceDestination', e.target.value)}
                  required
                >
                  <option value="">Sélectionner une agence</option>
                  {agences.map(agence => (
                    <option key={agence} value={agence}>{agence}</option>
                  ))}
                </select>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="form-group">
                <label className="form-label">Date de Départ</label>
                <input
                  type="datetime-local"
                  className="form-input"
                  value={formData.dateDepart || ''}
                  onChange={(e) => handleInputChange('dateDepart', e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Date d'Arrivée</label>
                <input
                  type="datetime-local"
                  className="form-input"
                  value={formData.dateArrivee || ''}
                  onChange={(e) => handleInputChange('dateArrivee', e.target.value)}
                  required
                />
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="form-group">
                <label className="form-label">Bus</label>
                <select
                  className="form-select"
                  value={formData.busId || ''}
                  onChange={(e) => handleInputChange('busId', parseInt(e.target.value))}
                  required
                >
                  <option value="">Sélectionner un bus</option>
                  {entities.bus.filter(bus => bus.statut === 'DISPONIBLE').map(bus => (
                    <option key={bus.id} value={bus.id}>
                      {bus.immatriculation} - {bus.type} ({bus.capacite} places)
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Chauffeur</label>
                <select
                  className="form-select"
                  value={formData.chauffeurId || ''}
                  onChange={(e) => handleInputChange('chauffeurId', parseInt(e.target.value))}
                  required
                >
                  <option value="">Sélectionner un chauffeur</option>
                  {entities.chauffeurs.filter(c => c.statut === 'DISPONIBLE').map(chauffeur => (
                    <option key={chauffeur.id} value={chauffeur.id}>
                      {chauffeur.prenom} {chauffeur.nom} ({chauffeur.agenceActuelle})
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Prix (FCFA)</label>
                <input
                  type="number"
                  className="form-input"
                  value={formData.prix || ''}
                  onChange={(e) => handleInputChange('prix', parseFloat(e.target.value))}
                  min="1000"
                  step="500"
                  required
                />
              </div>
            </div>
          </div>
        );

      case 'reservation':
        return (
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="form-group">
                <label className="form-label">Utilisateur</label>
                <select
                  className="form-select"
                  value={formData.utilisateurId || ''}
                  onChange={(e) => handleInputChange('utilisateurId', parseInt(e.target.value))}
                  required
                >
                  <option value="">Sélectionner un utilisateur</option>
                  {entities.utilisateurs.map(user => (
                    <option key={user.id} value={user.id}>
                      {user.prenom} {user.nom} - {user.typeClient} (Solde: {user.soldeCompte} FCFA)
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Trajet</label>
                <select
                  className="form-select"
                  value={formData.trajetId || ''}
                  onChange={(e) => handleInputChange('trajetId', parseInt(e.target.value))}
                  required
                >
                  <option value="">Sélectionner un trajet</option>
                  {entities.trajets.filter(t => t.statut === 'PLANIFIE').map(trajet => (
                    <option key={trajet.id} value={trajet.id}>
                      {trajet.agenceOrigine} → {trajet.agenceDestination} - {trajet.prix} FCFA
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Nombre de Places</label>
                <input
                  type="number"
                  className="form-input"
                  value={formData.nombrePlaces || ''}
                  onChange={(e) => handleInputChange('nombrePlaces', parseInt(e.target.value))}
                  min="1"
                  max="10"
                  required
                />
              </div>
            </div>
          </div>
        );

      case 'affectation':
        return (
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="form-group">
                <label className="form-label">Chauffeur</label>
                <select
                  className="form-select"
                  value={formData.chauffeurId || ''}
                  onChange={(e) => handleInputChange('chauffeurId', parseInt(e.target.value))}
                  required
                >
                  <option value="">Sélectionner un chauffeur</option>
                  {entities.chauffeurs.filter(c => c.statut === 'DISPONIBLE').map(chauffeur => (
                    <option key={chauffeur.id} value={chauffeur.id}>
                      {chauffeur.prenom} {chauffeur.nom} ({chauffeur.agenceActuelle})
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Trajet</label>
                <select
                  className="form-select"
                  value={formData.trajetId || ''}
                  onChange={(e) => handleInputChange('trajetId', parseInt(e.target.value))}
                  required
                >
                  <option value="">Sélectionner un trajet</option>
                  {entities.trajets.filter(t => t.statut === 'PLANIFIE').map(trajet => (
                    <option key={trajet.id} value={trajet.id}>
                      {trajet.agenceOrigine} → {trajet.agenceDestination}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Date d'Affectation</label>
                <input
                  type="datetime-local"
                  className="form-input"
                  value={formData.dateAffectation || ''}
                  onChange={(e) => handleInputChange('dateAffectation', e.target.value)}
                  required
                />
              </div>
            </div>
          </div>
        );

      case 'depart':
        return (
          <div className="space-y-4">
            <div className="form-group">
              <label className="form-label">Trajet à Faire Partir</label>
              <select
                className="form-select"
                value={formData.trajetId || ''}
                onChange={(e) => handleInputChange('trajetId', parseInt(e.target.value))}
                required
              >
                <option value="">Sélectionner un trajet</option>
                {entities.trajets.filter(t => t.statut === 'PLANIFIE').map(trajet => (
                  <option key={trajet.id} value={trajet.id}>
                    {trajet.agenceOrigine} → {trajet.agenceDestination} 
                    {trajet.bus && ` (${trajet.bus.type} - ${trajet.placesReservees}/${trajet.bus.capacite} places)`}
                  </option>
                ))}
              </select>
            </div>
          </div>
        );

      case 'transfert':
        return (
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="form-group">
                <label className="form-label">Bus à Transférer</label>
                <select
                  className="form-select"
                  value={formData.busId || ''}
                  onChange={(e) => handleInputChange('busId', parseInt(e.target.value))}
                  required
                >
                  <option value="">Sélectionner un bus</option>
                  {entities.bus.filter(bus => bus.statut === 'DISPONIBLE').map(bus => (
                    <option key={bus.id} value={bus.id}>
                      {bus.immatriculation} - {bus.type} (Actuellement à {bus.agenceActuelle})
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Agence de Destination</label>
                <select
                  className="form-select"
                  value={formData.agenceDestination || ''}
                  onChange={(e) => handleInputChange('agenceDestination', e.target.value)}
                  required
                >
                  <option value="">Sélectionner une agence</option>
                  {agences.map(agence => (
                    <option key={agence} value={agence}>{agence}</option>
                  ))}
                </select>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="form-group">
                <label className="form-label">Chauffeur</label>
                <select
                  className="form-select"
                  value={formData.chauffeurId || ''}
                  onChange={(e) => handleInputChange('chauffeurId', parseInt(e.target.value))}
                  required
                >
                  <option value="">Sélectionner un chauffeur</option>
                  {entities.chauffeurs.filter(c => c.statut === 'DISPONIBLE').map(chauffeur => (
                    <option key={chauffeur.id} value={chauffeur.id}>
                      {chauffeur.prenom} {chauffeur.nom} ({chauffeur.agenceActuelle})
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Date de Transfert</label>
                <input
                  type="datetime-local"
                  className="form-input"
                  value={formData.dateTransfert || ''}
                  onChange={(e) => handleInputChange('dateTransfert', e.target.value)}
                  required
                />
              </div>
            </div>
          </div>
        );

      case 'maintenance':
        return (
          <div className="space-y-4">
            <div className="form-group">
              <label className="form-label">Bus à Mettre en Maintenance</label>
              <select
                className="form-select"
                value={formData.busId || ''}
                onChange={(e) => handleInputChange('busId', parseInt(e.target.value))}
                required
              >
                <option value="">Sélectionner un bus</option>
                {entities.bus.map(bus => (
                  <option key={bus.id} value={bus.id}>
                    {bus.immatriculation} - {bus.type} (Statut: {bus.statut})
                  </option>
                ))}
              </select>
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  const currentPolicy = policies[selectedPolicy];
  const Icon = currentPolicy.icon;

  return (
    <div className="space-y-8 animate-fade-in">
      {/* En-tête */}
      <div>
        <h2 className="text-3xl font-bold text-gray-900 mb-2">
          Test des Politiques Mathématiques
        </h2>
        <p className="text-gray-600">
          Évaluez les 6 politiques implémentées selon leurs définitions mathématiques
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Sélection de la politique */}
        <div className="lg:col-span-1">
          <div className="card sticky top-4">
            <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
              <Settings className="h-5 w-5 mr-2" />
              Sélectionner une Politique
            </h3>
            <div className="space-y-2">
              {Object.entries(policies).map(([key, policy]) => {
                const PolicyIcon = policy.icon;
                return (
                  <button
                    key={key}
                    onClick={() => {
                      setSelectedPolicy(key);
                      setFormData({});
                      setResult(null);
                    }}
                    className={`w-full text-left p-3 rounded-lg border transition-all duration-200 ${
                      selectedPolicy === key
                        ? 'border-primary-500 bg-primary-50 text-primary-700'
                        : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50'
                    }`}
                  >
                    <div className="flex items-center space-x-3">
                      <PolicyIcon className={`h-5 w-5 ${
                        selectedPolicy === key ? 'text-primary-600' : 'text-gray-400'
                      }`} />
                      <div>
                        <div className="font-medium text-sm">{policy.name}</div>
                        <div className="text-xs text-gray-500 mt-1 line-clamp-2">
                          {policy.description.substring(0, 60)}...
                        </div>
                      </div>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        {/* Formulaire et résultats */}
        <div className="lg:col-span-2 space-y-6">
          {/* Description de la politique sélectionnée */}
          <div className="card bg-gray-50">
            <div className="flex items-start space-x-4">
              <div className={`p-3 rounded-full bg-white ${currentPolicy.color.replace('text-', 'text-')}`}>
                <Icon className={`h-6 w-6 ${currentPolicy.color}`} />
              </div>
              <div className="flex-1">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  {currentPolicy.name}
                </h3>
                <div className="bg-white p-3 rounded-lg border font-mono text-xs text-gray-700 overflow-x-auto">
                  {currentPolicy.description}
                </div>
              </div>
            </div>
          </div>

          {/* Formulaire */}
          <div className="card">
            <form onSubmit={handleSubmit}>
              <div className="card-header">
                <h3 className="text-lg font-semibold text-gray-900">
                  Paramètres de Test
                </h3>
              </div>
              
              {renderPolicyForm()}

              <div className="mt-6 pt-4 border-t border-gray-200">
                <button
                  type="submit"
                  disabled={isLoading}
                  className="btn-primary w-full"
                >
                  {isLoading ? (
                    <>
                      <div className="loading-spinner mr-2" />
                      Évaluation en cours...
                    </>
                  ) : (
                    <>
                      <Play className="h-4 w-4 mr-2" />
                      Évaluer la Politique
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>

          {/* Résultats */}
          {result && (
            <div className="card animate-slide-in-up">
              <div className="card-header">
                <h3 className="text-lg font-semibold text-gray-900 flex items-center">
                  {result.decision === 'ALLOW' ? (
                    <CheckCircle className="h-5 w-5 mr-2 text-success-600" />
                  ) : result.decision === 'DENY' ? (
                    <XCircle className="h-5 w-5 mr-2 text-error-600" />
                  ) : (
                    <Clock className="h-5 w-5 mr-2 text-warning-600" />
                  )}
                  Résultat de l'Évaluation
                </h3>
                <span className={`badge ${
                  result.decision === 'ALLOW' ? 'badge-success' : 
                  result.decision === 'DENY' ? 'badge-error' : 'badge-warning'
                }`}>
                  {result.decision}
                </span>
              </div>

              <div className={`policy-result ${result.decision.toLowerCase()}`}>
                <div className="policy-explanation">
                  {result.explication}
                </div>
              </div>

              {result.timestamp && (
                <div className="mt-4 text-xs text-gray-500">
                  Évalué le {new Date(result.timestamp).toLocaleString('fr-FR')}
                </div>
              )}

              {result.reservationId && (
                <div className="mt-4 p-3 bg-success-50 rounded-lg">
                  <p className="text-success-800 text-sm font-medium">
                    ✅ Réservation créée avec succès
                  </p>
                  <p className="text-success-700 text-xs mt-1">
                    ID: {result.reservationId} • N°: {result.numeroReservation}
                  </p>
                </div>
              )}

              {result.trajetId && (
                <div className="mt-4 p-3 bg-success-50 rounded-lg">
                  <p className="text-success-800 text-sm font-medium">
                    ✅ Trajet planifié avec succès
                  </p>
                  <p className="text-success-700 text-xs mt-1">
                    ID du trajet: {result.trajetId}
                  </p>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PolicyForm;