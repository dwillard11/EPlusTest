app.service("tripService", function ($http, $q, valutils,$filter) {
    this.loadTrip = function (tripid, tripmode, triptype, deptid) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: "operationconsole/operationconsole/loadTrip.do",
            params: {
                tripid: tripid,
                tripmode: tripmode,
                triptype: triptype,
                deptid: deptid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.releaseTripLock = function () {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: "operationconsole/operationconsole/releaseTripLock.do"
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;

    };
    this.loadFreights = function (tripid) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/loadFreights.do',
            params: {
                tripid: tripid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.loadCosts = function (tripid) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/loadCosts.do',
            params: {
                tripid: tripid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.loadDocs = function (tripid) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/loadDocs.do',
            params: {
                tripid: tripid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.loadDocsByType = function (tripid, type) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/loadDocs.do',
            params: {
                tripid: tripid,
                fileType: type
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.loadNotes = function (tripid) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/loadNotes.do',
            params: {
                tripid: tripid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.loadInvoices = function (tripid) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/loadInvoices.do',
            params: {
                tripid: tripid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.retrieveEventsByTripId = function (tripid) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: "operationconsole/operationconsole/retrieveEventsByTripId.do",
            params: {
                tripid: tripid,
                foobar: new Date().getTime()
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.loadTripEvents = function (tripid) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/retrieveEvents.do',
            params: {
                tripId: tripid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.loadTripEventById = function (eventId) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/retrieveEventById.do',
            params: {
                eid: eventId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.loadEventTemplateByQuoteType = function (quoteType) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: "operationconsole/operationconsole/getEventTree.do",
            params: {
                tripType: quoteType,
                foobar: new Date().getTime()
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.resetTripEventTree = function (tripid, entityIds) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/importEventByTripID.do',
            params: {
                tripId: tripid,
                entityIds: entityIds
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    };
    this.removeInvoice = function (id) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: 'operationconsole/operationconsole/removeInvoice.do',
            params: {
                id: id
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.removeTripEventById = function (eventId) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: 'operationconsole/operationconsole/removeEvent.do',
            params: {
                id: eventId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.removeTripEventByIds = function (eventIds) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: 'operationconsole/operationconsole/removeEvents.do',
            params: {
                ids: eventIds
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.markComplete = function (eventIds, markedComplete) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: 'operationconsole/operationconsole/markComplete.do',
            params: {
                ids: eventIds,
                markedComplete: markedComplete
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }

    this.createEventNotify = function (notify) {
        if (valutils.isEmptyOrUndefined(notify)) {
            return;
        }
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/createEventNotify.do',
            params: {
                id: notify.id,
                eventId: notify.eventId,
                name: notify.name,
                email: notify.email
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.updateEventNotify = function (notify) {
        if (valutils.isEmptyOrUndefined(notify)) {
            return;
        }
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/updateEventNotify.do',
            params: {
                id: notify.id,
                eventId: notify.eventId,
                name: notify.name,
                email: notify.email
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.removeEventNotify = function (notifyId) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: 'operationconsole/operationconsole/removeEventNotify.do',
            params: {
                notifyId: notifyId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.createTripCost = function (cost) {
        if (valutils.isEmptyOrUndefined(cost)) {
            return;
        }
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/createTripCost.do',
            params: {
                tripId: cost.tripId,
                eventId: cost.eventId,
                chargeCode: cost.chargeCode,
                estCost: cost.estCost,
                actCost: cost.actCost,
                actUsedCost: cost.actUsedCost,
                actUsedRate: cost.actUsedRate,
                estDateStr: $filter('date')(cost.estDate, 'yyyy-MM-dd'),
                actDateStr: $filter('date')(cost.actDate, 'yyyy-MM-dd'),
                actCurrency: cost.actCurrency,
                estCurrency: cost.estCurrency,
                description: cost.description,
                visible: cost.visible,
                linkedEntity: cost.linkedEntity,
                linkedEntityContact: cost.linkedEntityContact
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.updateTripCost = function (cost) {
        if (valutils.isEmptyOrUndefined(cost)) {
            return;
        }
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/updateTripCost.do',
            params: {
                id: cost.id,
                tripId: cost.tripId,
                eventId: cost.eventId,
                chargeCode: cost.chargeCode,
                estCost: cost.estCost,
                actCost: cost.actCost,
                actUsedCost: cost.actUsedCost,
                actUsedRate: cost.actUsedRate,
                estDateStr: $filter('date')(cost.estDate, 'yyyy-MM-dd'),
                actDateStr: $filter('date')(cost.actDate, 'yyyy-MM-dd'),
                actCurrency: cost.actCurrency,
                estCurrency: cost.estCurrency,
                description: cost.description,
                visible: cost.visible,
                linkedEntity: cost.linkedEntity,
                linkedEntityContact: cost.linkedEntityContact
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.removeTripCost = function (costId) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: 'operationconsole/operationconsole/removeTripCost.do',
            params: {
                costId: costId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.replicateEmailAddressForAllEvents = function (addresslist, eventid, tripid) {
        var notifies = "";
        if (!valutils.isEmptyOrUndefined(addresslist)) {
            notifies = JSON.stringify(addresslist);
        }
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/replicateEmailAddressForAllEvents.do',
            params: {
                tripId: tripid,
                eventId: eventid,
                notifies: notifies
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.createFreight = function (freight) {
        if (valutils.isEmptyOrUndefined(freight)) {
            return;
        }
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/createFreight.do',
            params: {
                tripId: freight.tripId,
                id: freight.id,
                item: freight.item,
                description: freight.description,
                estimatedCost: freight.estimatedCost,
                estimatedWeight: freight.estimatedWeight,
                estimatedCurrency: freight.estimatedCurrency,
                estimatedDimension: freight.estimatedDimension,
                estimatedPieces: freight.estimatedPieces,
                estimatedUOM: freight.estimatedUOM,
                estimatedCurrency: freight.estimatedCurrency,
                bagtag: freight.bagtag,
                estimatedPieces: freight.estimatedPieces,
                actualDimension: freight.actualDimension,
                actualCurrency: freight.actualCurrency,
                usdCost: freight.usdCost,
                usdRate: freight.usdRate,
                actualWeight: freight.actualWeight,
                actualCost: freight.actualCost,
                actualPieces: freight.actualPieces,
                actualUOM: freight.actualUOM,
                estimatedChargeWt: freight.estimatedChargeWt,
                actualChargeWt: freight.actualChargeWt
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.updateFreight = function (freight) {
        if (valutils.isEmptyOrUndefined(freight)) {
            return;
        }
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/updateFreight.do',
            params: {
                tripId: freight.tripId,
                id: freight.id,
                item: freight.item,
                description: freight.description,
                estimatedCost: freight.estimatedCost,
                estimatedWeight: freight.estimatedWeight,
                estimatedCurrency: freight.estimatedCurrency,
                estimatedDimension: freight.estimatedDimension,
                estimatedPieces: freight.estimatedPieces,
                estimatedUOM: freight.estimatedUOM,
                estimatedCurrency: freight.estimatedCurrency,
                bagtag: freight.bagtag,
                estimatedPieces: freight.estimatedPieces,
                actualDimension: freight.actualDimension,
                actualCurrency: freight.actualCurrency,
                usdCost: freight.usdCost,
                usdRate: freight.usdRate,
                actualWeight: freight.actualWeight,
                actualCost: freight.actualCost,
                actualPieces: freight.actualPieces,
                actualUOM: freight.actualUOM,
                estimatedChargeWt: freight.estimatedChargeWt,
                actualChargeWt: freight.actualChargeWt
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.removeFreight = function (freightId) {
        var defer = $q.defer();
        $http({
            method: 'GET',
            url: 'operationconsole/operationconsole/removeFreight.do',
            params: {
                freightId: freightId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.deleteDocument = function (id, type) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/removeTripDocument.do',
            params: {
                docId: id,
                docType: type
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.saveTripTemplateName = function (templateName,tripid,eventTemplate) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/saveTripTemplateName.do',
            params: {
                templateName: templateName,
                eventTemplate: eventTemplate,
                tripid: tripid
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.retrieveTripEventTemplates = function (tripType) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/retrieveTripEventTemplates.do',
            params: {
                tripType: tripType
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
    this.retrieveTripTemplates = function (departmentId) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/retrieveTripTemplates.do',
            params: {
                departmentId: departmentId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }

    this.updateTripDivision = function (tripid,departmentId) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/updateTripDivision.do',
            params: {
                tripId: tripid,
                departmentId: departmentId
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }

    this.updateTripMeasureSystem = function (tripId, measurCode) {
        var defer = $q.defer();
        $http({
            method: 'POST',
            url: 'operationconsole/operationconsole/updateTripMeasureSystem.do',
            params: {
                tripId: tripId,
                measureCode: measurCode
            }
        }).success(function (data) {
            defer.resolve(data);
        }).error(function (data) {
            defer.reject(data);
        });
        return defer.promise;
    }
})